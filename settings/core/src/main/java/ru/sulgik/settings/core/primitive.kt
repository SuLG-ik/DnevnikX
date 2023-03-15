package ru.sulgik.settings.core

import kotlin.reflect.KType
import kotlin.reflect.typeOf


private class PrimitiveSettingSerializer<Setting : Any, Serial : Any>(
    name: String,
    defaultValue: Setting,
    private val constructor: (Serial) -> Setting,
    private val deconstructor: (Setting) -> Serial,
    type: KType,
    kind: SettingsKind,
) : SingleSettingSerializer<Setting> {

    override val descriptor: SingleSettingsDescriptor<Setting> = SingleSettingsDescriptor(
        type, kind, name, defaultValue
    )

    @Suppress("UNCHECKED_CAST")
    override fun deserialize(value: SingleSettingsValue): Setting =
        constructor(value.value as Serial)

    override fun serialize(value: Setting): SingleSettingsValue =
        SingleSettingsValue(deconstructor(value))

}

fun <T : Any> stringSettingSerializer(
    name: String,
    defaultValue: T,
    constructor: (String) -> T,
    deconstructor: (T) -> String,
    type: KType,
): SingleSettingSerializer<T> {
    return PrimitiveSettingSerializer(
        name = name,
        defaultValue = defaultValue,
        constructor = constructor,
        deconstructor = deconstructor,
        type = type,
        kind = SettingsKind.STRING,
    )
}

inline fun <reified T : Any> stringSettingSerializer(
    name: String,
    defaultValue: T,
    noinline constructor: (String) -> T,
    noinline deconstructor: (T) -> String,
): SingleSettingSerializer<T> {
    return stringSettingSerializer(
        name = name,
        defaultValue = defaultValue,
        constructor = constructor,
        deconstructor = deconstructor,
        type = typeOf<T>()
    )
}

fun <T : Any> booleanSettingSerializer(
    name: String,
    defaultValue: T,
    constructor: (Boolean) -> T,
    deconstructor: (T) -> Boolean,
    type: KType,
): SingleSettingSerializer<T> {
    return PrimitiveSettingSerializer(
        name = name,
        defaultValue = defaultValue,
        constructor = constructor,
        deconstructor = deconstructor,
        type = type,
        kind = SettingsKind.BOOLEAN,
    )
}

inline fun <reified T : Any> booleanSettingSerializer(
    name: String,
    defaultValue: T,
    noinline constructor: (Boolean) -> T,
    noinline deconstructor: (T) -> Boolean,
): SingleSettingSerializer<T> {
    return booleanSettingSerializer(
        name = name,
        defaultValue = defaultValue,
        constructor = constructor,
        deconstructor = deconstructor,
        type = typeOf<T>()
    )
}
