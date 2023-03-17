package ru.sulgik.application.settings

import ru.sulgik.settings.core.BooleanSingleSettingSerializer
import kotlin.reflect.typeOf

object ApplicationSettingsSerializer :
    BooleanSingleSettingSerializer<NestedScreenTransitionSetting>(
        name = "application_nested_screen_transition",
        defaultValue = NestedScreenTransitionSetting(false),
        constructor = ::NestedScreenTransitionSetting,
        deconstructor = NestedScreenTransitionSetting::enabled,
        type = typeOf<NestedScreenTransitionSetting>(),
    )
