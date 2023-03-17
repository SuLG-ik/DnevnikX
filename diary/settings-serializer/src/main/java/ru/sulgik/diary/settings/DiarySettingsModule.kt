package ru.sulgik.diary.settings

import org.koin.core.module.dsl.withOptions
import org.koin.dsl.module
import ru.sulgik.settings.core.SingleSettingSerializer

class DiarySettingsModule {

    val module = module {
        single { DiaryPagerEnabledSettingSerializer }.withOptions {
            secondaryTypes += SingleSettingSerializer::class
        }
    }

}
