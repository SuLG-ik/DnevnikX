package ru.sulgik.finalmarks.domain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.sulgik.auth.core.AuthScope
import ru.sulgik.auth.ktor.Client
import ru.sulgik.common.safeBody
import ru.sulgik.finalmarks.domain.data.FinalMarksOutput

class KtorRemoteFinalMarksRepository(
    private val client: Client,
) : RemoteFinalMarksRepository {

    override suspend fun getFinalMarks(auth: AuthScope): FinalMarksOutput {
        val response = client.authorizedGet(auth, "getfinalassessments")
        val body = response.safeBody<GetFinalMarksResponse>().students.firstNotNullOf { it.value }
        return FinalMarksOutput(
            body.items.map { lesson ->
                FinalMarksOutput.Lesson(
                    title = lesson.name,
                    marks = lesson.marks.map { mark ->
                        FinalMarksOutput.Mark(
                            mark = mark.mark,
                            value = mark.value,
                            period = mark.period
                        )
                    }
                )
            }
        )
    }


    @Serializable
    private class GetFinalMarksResponse(
        val students: Map<String, Student>,
    )

    @Serializable
    private class Student(
        val items: List<Lesson>,
    )

    @Serializable
    class Lesson(
        val name: String,
        @SerialName("assessments")
        val marks: List<Mark>,
    )

    @Serializable
    class Mark(
        @SerialName("value")
        val mark: String,
        @SerialName("convert")
        val value: Int,
        val period: String,
    )


}