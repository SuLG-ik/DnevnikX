package ru.sulgik.dnevnikx.repository.periods.ktor

import io.ktor.client.request.parameter
import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.koin.core.annotation.Single
import ru.sulgik.dnevnikx.data.AuthScope
import ru.sulgik.dnevnikx.platform.DatePeriod
import ru.sulgik.dnevnikx.repository.Client
import ru.sulgik.dnevnikx.repository.data.GetPeriodsOutput
import ru.sulgik.dnevnikx.repository.data.safeBody
import ru.sulgik.dnevnikx.repository.periods.RemotePeriodsRepository
import ru.sulgik.dnevnikx.utils.CustomLocalDateSerializer

@Single
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