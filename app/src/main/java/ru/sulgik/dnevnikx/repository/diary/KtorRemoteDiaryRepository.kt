package ru.sulgik.dnevnikx.repository.diary

import io.ktor.client.request.parameter
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.toJavaLocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.koin.core.annotation.Single
import ru.sulgik.dnevnikx.data.AuthScope
import ru.sulgik.dnevnikx.repository.Client
import ru.sulgik.dnevnikx.platform.DatePeriod
import ru.sulgik.dnevnikx.repository.data.DiaryOutput
import ru.sulgik.dnevnikx.platform.TimePeriod
import ru.sulgik.dnevnikx.repository.data.safeBody
import ru.sulgik.dnevnikx.utils.CustomLocalDateSerializer

@Single
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
            student.days.map { item ->
                DiaryOutput.Item(
                    item.key,
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
        val days: Map<@Serializable(CustomLocalDateSerializer::class) LocalDate, Day>,
    )

    @Serializable
    data class Day(
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