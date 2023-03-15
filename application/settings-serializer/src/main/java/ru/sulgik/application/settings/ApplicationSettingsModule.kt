package ru.sulgik.application.settings

import org.koin.dsl.module

class ApplicationSettingsModule {

    val module = module {
        single { ApplicationSettingsSerializer }
    }

}