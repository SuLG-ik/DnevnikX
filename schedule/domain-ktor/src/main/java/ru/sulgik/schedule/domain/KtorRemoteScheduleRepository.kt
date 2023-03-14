package ru.sulgik.schedule.domain

import io.ktor.client.request.parameter
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.toJavaLocalDate
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.sulgik.auth.core.AuthScope
import ru.sulgik.auth.ktor.Client
import ru.sulgik.common.CustomLocalDateSerializer
import ru.sulgik.common.platform.DatePeriod
import ru.sulgik.common.platform.TimePeriod
import ru.sulgik.common.safeBody
import ru.sulgik.dnevnikx.repository.data.GetScheduleOutput

class KtorRemoteScheduleRepository(
    private val client: Client,
) : RemoteScheduleRepository {
    override suspend fun getSchedule(
        auth: AuthScope,
        period: DatePeriod,
        classGroup: String,
    ): GetScheduleOutput {
        val response = client.authorizedGet(auth, "getschedule") {
            parameter(
                "days",
                "${
                    period.start.toJavaLocalDate().format(CustomLocalDateSerializer.formatter)
                }-${period.end.toJavaLocalDate().format(CustomLocalDateSerializer.formatter)}"
            )
            parameter("rings", true)
            parameter("class", classGroup)
        }
        val body = response.safeBody<GetScheduleBody>()
        val items = body.days.map { (key, value) ->
            val groups = mutableMapOf<String, GetScheduleOutput.LessonGroup>()
            value.items.forEach { lesson ->
                val output = GetScheduleOutput.Lesson(
                    title = lesson.name,
                    time = TimePeriod(lesson.start, lesson.end),
                    teacher = lesson.teacher,
                    group = lesson.fullGroup,
                )
                groups.computeIfPresent(lesson.num) { _, value ->
                    value.copy(lessons = value.lessons + output)
                }
                groups.computeIfAbsent(lesson.num) {
                    GetScheduleOutput.LessonGroup(
                        number = lesson.num,
                        lessons = listOf(output)
                    )
                }
            }
            GetScheduleOutput.Item(
                date = key,
                title = value.title,
                lessonGroups = groups.map { it.value },
            )
        }
        return GetScheduleOutput(
            schedule = items,
        )
    }
}

@Serializable
private class GetScheduleBody(
    val days: Map<@Contextual LocalDate, Item> = emptyMap(),
) {

    @Serializable
    data class Item(
        val title: String = "",
        val items: List<Lesson> = emptyList(),
    )

    @Serializable
    data class Lesson(
        val name: String = "",
        val num: String = "",
        val room: String = "",
        val teacher: String = "",
        @SerialName("starttime")
        val start: LocalTime,
        @SerialName("endtime")
        val end: LocalTime,
        @SerialName("grp_short")
        val group: String? = null,
        @SerialName("grp")
        val fullGroup: String? = null,
    )

}