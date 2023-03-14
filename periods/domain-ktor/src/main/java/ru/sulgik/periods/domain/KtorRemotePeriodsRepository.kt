package ru.sulgik.periods.domain

import io.ktor.client.request.parameter
import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.sulgik.auth.core.AuthScope
import ru.sulgik.auth.ktor.Client
import ru.sulgik.common.CustomLocalDateSerializer
import ru.sulgik.common.platform.DatePeriod
import ru.sulgik.common.safeBody
import ru.sulgik.periods.domain.data.GetPeriodsOutput

class KtorRemotePeriodsRepository(
    private val client: Client,
) : RemotePeriodsRepository {

    override suspend fun getPeriods(auth: AuthScope): GetPeriodsOutput {
        val response = client.authorizedGet(auth, "getperiods") {
            parameter("weeks", true)
        }
        val body = response.safeBody<GetPeriodResponse>()
        return GetPeriodsOutput(
            periods = body.students.firstOrNull()?.periods?.map { period ->
                GetPeriodsOutput.Period(
                    title = period.title,
                    period = DatePeriod(period.start, period.end),
                    period.weeks.map { week ->
                        DatePeriod(week.start, week.end)
                    }
                )
            } ?: emptyList()
        )
    }

}


@Serializable
private class GetPeriodResponse(
    val students: List<Student> = emptyList(),
) {
    @Serializable
    class Student(
        val periods: List<Period> = emptyList(),
    )

    @Serializable
    class Period(
        @SerialName("fullname")
        val title: String,
        @Serializable(CustomLocalDateSerializer::class)
        val start: LocalDate,
        @Serializable(CustomLocalDateSerializer::class)
        val end: LocalDate,
        val weeks: List<Week>,
    )

    @Serializable
    class Week(
        @Serializable(CustomLocalDateSerializer::class)
        val start: LocalDate,
        @Serializable(CustomLocalDateSerializer::class)
        val end: LocalDate,
    )
}