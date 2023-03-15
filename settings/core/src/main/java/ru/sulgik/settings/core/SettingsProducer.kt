package ru.sulgik.settings.core

interface SingleSettingSerializer<T : Any> {

    val descriptor: SingleSettingsDescriptor<T>

    fun serialize(value: T): SingleSettingsValue

    fun deserialize(value: SingleSettingsValue): T

}