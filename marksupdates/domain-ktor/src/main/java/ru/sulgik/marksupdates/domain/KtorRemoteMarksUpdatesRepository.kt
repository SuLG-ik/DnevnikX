package ru.sulgik.marksupdates.domain

import io.ktor.client.request.parameter
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.sulgik.auth.core.AuthScope
import ru.sulgik.auth.ktor.Client
import ru.sulgik.common.safeBody
import ru.sulgik.marksupdates.domain.data.MarksUpdatesOutput


class KtorRemoteMarksUpdatesRepository(
    private val client: Client,
) : RemoteMarksUpdatesRepository {

    override suspend fun getMarksUpdates(
        auth: AuthScope,
        page: Int,
        limit: Int,
    ): MarksUpdatesOutput {
        val response = client.authorizedGet(auth, "getupdates") {
            parameter("page", page)
            parameter("limit", limit)
        }
        val body = response.safeBody<MarksUpdatesResponse>()
        val student =
            body.students.values.firstOrNull() ?: return MarksUpdatesOutput(
                emptyList(),
                emptyList()
            )
        return MarksUpdatesOutput(
            latest = student.updates.latest.map { it.toData() },
            old = student.updates.old.map { it.toData() }
        )
    }


}


private fun MarksUpdatesResponse.MarkUpdate.toData(): MarksUpdatesOutput.MarkUpdate {
    return MarksUpdatesOutput.MarkUpdate(
        lesson = MarksUpdatesOutput.Lesson(
            name = lessonName,
            date = date,
        ),
        currentMark = MarksUpdatesOutput.Mark(
            mark = mark,
            value = value
        ),
        previousMark = if (previousMark != null && previousValue != null) {
            MarksUpdatesOutput.Mark(
                mark = previousMark,
                value = previousValue,
            )
        } else null
    )
}


@Serializable
class MarksUpdatesResponse(
    val students: Map<String, Student>,
) {
    @Serializable
    data class Student(
        val updates: MarksUpdates,
    )

    @Serializable
    data class MarksUpdates(
        val latest: List<MarkUpdate>,
        val old: List<MarkUpdate>,
    )

    @Serializable
    class MarkUpdate(
        @SerialName("lesson")
        val lessonName: String,
        @Contextual
        val date: LocalDate,
        val mark: String,
        @SerialName("convert")
        val value: Int,
        @SerialName("prevmark")
        val previousMark: String? = null,
        @SerialName("prevconvert")
        val previousValue: Int? = null,
    )


}

