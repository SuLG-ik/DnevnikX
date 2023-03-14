package ru.sulgik.periods.domain.room

import ru.sulgik.auth.core.AuthScope
import ru.sulgik.common.platform.DatePeriod
import ru.sulgik.periods.domain.LocalPeriodsRepository
import ru.sulgik.periods.domain.data.GetPeriodsOutput


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