package ru.sulgik.koin.mvi

import org.koin.dsl.module
import ru.sulgik.about.mvi.AboutMVIModule
import ru.sulgik.account.mvi.AccountMVIModule
import ru.sulgik.account.selector.mvi.AccountSelectorMVIModule
import ru.sulgik.application.mvi.ApplicationMVIModule
import ru.sulgik.auth.mvi.AuthMVIModule
import ru.sulgik.diary.mvi.DiaryMVIModule
import ru.sulgik.experimentalsettings.mvi.ExperimentalSettingsMVIModule
import ru.sulgik.finalmarks.mvi.FinalMarksMVIModule
import ru.sulgik.main.mvi.MainMVIModule
import ru.sulgik.marks.mvi.MarksMVIModule
import ru.sulgik.marksedit.mvi.MarksEditMVIModule
import ru.sulgik.marksupdates.MarksUpdatesMVIModule
import ru.sulgik.schedule.add.mvi.ScheduleAddMVIModule
import ru.sulgik.schedule.mvi.ScheduleMVIModule

class KoinMVIModule {

    val module = module {
        includes(DiaryMVIModule().module)
        includes(AboutMVIModule().module)
        includes(MarksMVIModule().module)
        includes(MarksEditMVIModule().module)
        includes(FinalMarksMVIModule().module)
        includes(ScheduleMVIModule().module)
        includes(AuthMVIModule().module)
        includes(AccountMVIModule().module)
        includes(AccountSelectorMVIModule().module)
        includes(MainMVIModule().module)
        includes(ApplicationMVIModule().module)
        includes(ExperimentalSettingsMVIModule().module)
        includes(MarksUpdatesMVIModule().module)
        includes(ScheduleAddMVIModule().module)
    }

}