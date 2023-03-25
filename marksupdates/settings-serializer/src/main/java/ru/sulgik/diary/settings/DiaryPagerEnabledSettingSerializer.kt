package ru.sulgik.diary.settings

import ru.sulgik.settings.core.BooleanSingleSettingSerializer
import kotlin.reflect.typeOf

object DiaryPagerEnabledSettingSerializer :
    BooleanSingleSettingSerializer<DiaryPagerEnabledSetting>(
        name = "diary_pager_enabled",
        defaultValue = DiaryPagerEnabledSetting(false),
        constructor = ::DiaryPagerEnabledSetting,
        deconstructor = DiaryPagerEnabledSetting::enabled,
        type = typeOf<DiaryPagerEnabledSetting>(),
    )