package ru.sulgik.dnevnikx.repository.periods.room

import org.koin.core.annotation.Single
import ru.sulgik.dnevnikx.data.AuthScope
import ru.sulgik.dnevnikx.platform.DatePeriod
import ru.sulgik.dnevnikx.repository.data.GetPeriodsOutput
import ru.sulgik.dnevnikx.repository.periods.LocalPeriodsRepository


@Single
class RoomLocalPeriodsRepository(
    private val periodDao: PeriodDao,
) : LocalPeriodsRepository {

    override suspend fun getPeriods(auth: AuthScope): GetPeriodsOutput? {
        val periods = periodDao.getAllForAccount(auth.id)
        if (periods.isEmpty()) return null
        return GetPeriodsOutput(periods.map { period ->
            GetPeriodsOutput.Period(
                title = period.host.title,
                period = DatePeriod(
                    start = period.host.start,
                    end = period.host.end,
                ),
                nestedPeriods = period.nested.map { nested ->
                    DatePeriod(
                        start = nested.start,
                        end = nested.end
                    )
                }
            )
        })
    }

    override suspend fun savePeriods(auth: AuthScope, periods: GetPeriodsOutput) {
        val mappedPeriods = periods.periods.associate {
            HostPeriodEntity(
                title = it.title,
                start = it.period.start,
                end = it.period.end,
                accountId = auth.id,
            ) to it.nestedPeriods
        }
        periodDao.savePeriods(
            auth.id,
            mappedPeriods.keys.toList(),
            mappedPeriods.values.toList(),
        )
    }

}