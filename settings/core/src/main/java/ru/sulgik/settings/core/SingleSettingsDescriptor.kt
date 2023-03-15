package ru.sulgik.settings.core

import kotlin.reflect.KType
import kotlin.reflect.typeOf


data class SingleSettingsDescriptor<T : Any>(
    val type: KType,
    val kind: SettingsKind,
    val name: String,
    val defaultValue: T,
)

inline fun <reified T : Any> SingleSettingsDescriptor(
    kind: SettingsKind,
    name: String,
    defaultValue: T,
): SingleSettingsDescriptor<T> {
    return SingleSettingsDescriptor(typeOf<T>(), kind, name, defaultValue)
}

data class SingleSettingsValue(
    val value: Any,
)

enum class SettingsKind {
    STRING, BOOLEAN;
}



