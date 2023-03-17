package ru.sulgik.application.settings

import org.koin.core.module.dsl.withOptions
import org.koin.dsl.module
import ru.sulgik.settings.core.SingleSettingSerializer

class ApplicationSettingsModule {

    val module = module {
        single { ApplicationSettingsSerializer }.withOptions {
            secondaryTypes += SingleSettingSerializer::class
        }
    }

}