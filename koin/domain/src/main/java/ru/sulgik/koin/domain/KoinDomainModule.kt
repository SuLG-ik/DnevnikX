package ru.sulgik.koin.domain

import org.koin.dsl.module
import ru.sulgik.about.domain.AboutBuiltInModule
import ru.sulgik.account.domain.AccountCachedModule
import ru.sulgik.account.domain.AccountLocalModule
import ru.sulgik.account.domain.AccountRemoteModule
import ru.sulgik.account.domain.AccountSessionLocalModule
import ru.sulgik.auth.domain.AuthLocalModule
import ru.sulgik.auth.domain.AuthRemoteModule
import ru.sulgik.diary.domain.DiaryCachedModule
import ru.sulgik.diary.domain.DiaryLocalModule
import ru.sulgik.diary.domain.DiaryRemoteModule
import ru.sulgik.finalmarks.domain.FinalMarksCachedModule
import ru.sulgik.finalmarks.domain.FinalMarksLocalModule
import ru.sulgik.finalmarks.domain.FinalMarksRemoteModule
import ru.sulgik.marks.domain.MarksCachedModule
import ru.sulgik.marks.domain.MarksLocalModule
import ru.sulgik.marks.domain.MarksRemoteModule
import ru.sulgik.marksupdates.domain.MarksUpdatesCachedModule
import ru.sulgik.marksupdates.domain.MarksUpdatesRemoteModule
import ru.sulgik.periods.domain.PeriodsCachedModule
import ru.sulgik.periods.domain.PeriodsRemoteModule
import ru.sulgik.periods.domain.room.PeriodsLocalModule
import ru.sulgik.schedule.add.domain.ScheduleAddCachedModule
import ru.sulgik.schedule.add.domain.ScheduleAddLocalModule
import ru.sulgik.schedule.domain.ScheduleRemoteModule

class KoinDomainModule {

    val module = module {
        includes(
            DiaryLocalModule().module,
            DiaryRemoteModule().module,
            DiaryCachedModule().module
        )
        includes(
            MarksLocalModule().module,
            MarksRemoteModule().module,
            MarksCachedModule().module
        )
        includes(
            FinalMarksLocalModule().module,
            FinalMarksRemoteModule().module,
            FinalMarksCachedModule().module
        )
        includes(ScheduleRemoteModule().module)
        includes(
            PeriodsRemoteModule().module,
            PeriodsLocalModule().module,
            PeriodsCachedModule().module
        )
        includes(AuthLocalModule().module, AuthRemoteModule().module)
        includes(
            AccountLocalModule().module,
            AccountRemoteModule().module,
            AccountSessionLocalModule().module,
            AccountCachedModule().module,
        )
        includes(
            MarksUpdatesCachedModule().module,
            MarksUpdatesRemoteModule().module,
        )
        includes(AboutBuiltInModule().module)
        includes(
            ScheduleAddLocalModule().module,
            ScheduleAddCachedModule().module,
        )
    }

}