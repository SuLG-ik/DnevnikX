package ru.sulgik.diary.domain

import io.ktor.client.request.parameter
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.toJavaLocalDate
import kotlinx.serialization.Contextual
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames
import ru.sulgik.auth.core.AuthScope
import ru.sulgik.auth.ktor.Client
import ru.sulgik.common.CustomLocalDateSerializer
import ru.sulgik.common.platform.DatePeriod
import ru.sulgik.common.platform.TimePeriod
import ru.sulgik.common.safeBody
import ru.sulgik.diary.domain.data.DiaryOutput

class KtorRemoteDiaryRepository(
    private val client: Client,
) : RemoteDiaryRepository {

    override suspend fun getDiary(auth: AuthScope, period: DatePeriod): DiaryOutput {
        val response = client.authorizedGet(auth, "getdiary") {
            parameter(
                "days",
                "${
                    period.start.toJavaLocalDate().format(CustomLocalDateSerializer.formatter)
                }-${period.end.toJavaLocalDate().format(CustomLocalDateSerializer.formatter)}"
            )
            parameter("rings", true)
        }
        val body = response.safeBody<GetDiaryBody>()
        val student = body.students.firstNotNullOf { it.value }
        return DiaryOutput(
            period = period,
            diary = student.days.map { item ->
                DiaryOutput.Item(
                    item.key,
                    item.value.let {
                        if ((it.alert == null || it.alert == "today") && it.message == null) {
                            null
                        } else {
                            DiaryOutput.Alert(
                                it.alert ?: "",
                                it.message ?: ""
                            )
                        }
                    },
                    item.value.items.map { lesson ->
                        DiaryOutput.Lesson(
                            number = lesson.value.number,
                            title = lesson.value.title,
                            time = TimePeriod(lesson.value.start, lesson.value.end),
                            homework = lesson.value.homework.map { homework ->
                                DiaryOutput.Homework(homework.value.text)
                            },
                            files = lesson.value.files.map { file ->
                                DiaryOutput.File(file.name, file.url)
                            },
                            marks = lesson.value.marks.map { mark ->
                                DiaryOutput.Mark(mark.mark, mark.value)
                            }
                        )
                    }
                )
            }
        )
    }
}


@Serializable
private data class GetDiaryBody(
    val students: Map<String, Student>,
) {
    @Serializable
    data class Student(
        val days: Map<@Contextual LocalDate, Day>,
    )

    @Serializable
    @OptIn(ExperimentalSerializationApi::class)
    data class Day constructor(
        val alert: String? = null,
        @JsonNames("holiday_name", "message")
        val message: String? = null,
        val items: Map<String, Lesson>,
    )

    @Serializable
    data class Lesson(
        @SerialName("name")
        val title: String,
        @SerialName("num")
        val number: String,
        @SerialName("starttime")
        val start: LocalTime,
        @SerialName("endtime")
        val end: LocalTime,
        val homework: Map<String, Homework>,
        val files: List<File>,
        @SerialName("assessments")
        val marks: List<Mark> = emptyList(),
    )

    @Serializable
    class Homework(
        @SerialName("value")
        val text: String,

        )

    @Serializable
    data class File(
        @SerialName("filename")
        val name: String,
        @SerialName("link")
        val url: String,
    )

    @Serializable
    class Mark(
        @SerialName("value")
        val mark: String = "0",
        @SerialName("convert")
        val value: Int = 0,
    )
}