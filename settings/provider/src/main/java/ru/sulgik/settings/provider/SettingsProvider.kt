package ru.sulgik.settings.provider

import kotlinx.coroutines.flow.Flow
import ru.sulgik.auth.core.AuthScope
import kotlin.reflect.KType
import kotlin.reflect.typeOf

interface SettingsProvider {

    fun <T : Any> getSettingFlow(authScope: AuthScope, type: KType): Flow<T>

    suspend fun <T : Any> provide(authScope: AuthScope, value: T, type: KType)

}

suspend inline fun <reified T : Any> SettingsProvider.provide(authScope: AuthScope, value: T) {
    return provide(authScope, value, typeOf<T>())
}

inline fun <reified T : Any> SettingsProvider.getSettingFlow(authScope: AuthScope): Flow<T> {
    return getSettingFlow(authScope, typeOf<T>())
}