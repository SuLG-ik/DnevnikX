package ru.sulgik.dnevnikx.repository.marks

import io.ktor.client.request.parameter
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.koin.core.annotation.Single
import ru.sulgik.dnevnikx.data.AuthScope
import ru.sulgik.dnevnikx.repository.Client
import ru.sulgik.dnevnikx.platform.DatePeriod
import ru.sulgik.dnevnikx.repository.data.MarksOutput
import ru.sulgik.dnevnikx.repository.data.safeBody
import ru.sulgik.dnevnikx.utils.CustomLocalDateSerializer

@Single
class KtorRemoteMarksRepository(
    private val client: Client,
) : RemoteMarksRepository {
    override suspend fun getMarks(auth: AuthScope, period: DatePeriod): MarksOutput {
        val response = client.authorizedGet(auth, "getmarks") {
            parameter(
                "days",
                "${
                    period.start.toJavaLocalDate().format(CustomLocalDateSerializer.formatter)
                }-${period.end.toJavaLocalDate().format(CustomLocalDateSerializer.formatter)}"
            )
        }
        val student = response.safeBody<MarksResponse>().students.firstNotNullOf { it.value }
        return MarksOutput(student.lessons.map { lesson ->
            MarksOutput.Lesson(
                title = lesson.title,
                average = lesson.average,
                averageValue = lesson.averageValue,
                marks = lesson.marks.map { mark ->
                    MarksOutput.Mark(
                        mark = mark.mark,
                        value = mark.value,
                        date = mark.date,
                        message = mark.message
                    )
                }
            )
        })
    }
}

@Serializable
private class MarksResponse(
    val students: Map<String, Student>,
)

@Serializable
private class Student(
    val lessons: List<Lesson>,
)

@Serializable
private class Lesson(
    @SerialName("name")
    val title: String = "0",
    val average: String = "0",
    @SerialName("averageConvert")
    val averageValue: Int = 0,
    val marks: List<Mark> = emptyList(),
)

@Serializable
private class Mark(
    @SerialName("value")
    val mark: String = "0",
    @SerialName("convert")
    val value: Int = 0,
    val date: LocalDate,
    @SerialName("lesson_comment")
    val message: String? = null,
)