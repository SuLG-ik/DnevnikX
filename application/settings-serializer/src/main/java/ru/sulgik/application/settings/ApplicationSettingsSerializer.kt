package ru.sulgik.application.settings

import ru.sulgik.settings.core.booleanSettingSerializer

val ApplicationSettingsSerializer = booleanSettingSerializer(
    name = "application_nested_screen_transition",
    defaultValue = NestedScreenTransactionSetting(false),
    constructor = ::NestedScreenTransactionSetting,
    deconstructor = NestedScreenTransactionSetting::enabled,
)