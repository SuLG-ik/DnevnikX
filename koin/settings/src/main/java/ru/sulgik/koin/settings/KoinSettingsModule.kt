package ru.sulgik.koin.settings

import org.koin.dsl.module
import ru.sulgik.application.settings.ApplicationSettingsModule
import ru.sulgik.diary.settings.DiarySettingsModule

class KoinSettingsModule {

    val module = module {
        includes(ApplicationSettingsModule().module)
        includes(DiarySettingsModule().module)
    }

}