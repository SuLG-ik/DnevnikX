package ru.sulgik.koin.settings

import org.koin.dsl.module
import ru.sulgik.application.settings.ApplicationSettingsModule

class KoinSettingsModule {

    val module = module {
        includes(ApplicationSettingsModule().module)
    }

}