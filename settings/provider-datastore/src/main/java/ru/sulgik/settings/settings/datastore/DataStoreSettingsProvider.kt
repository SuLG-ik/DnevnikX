package ru.sulgik.settings.settings.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.sulgik.auth.core.AuthScope
import ru.sulgik.settings.core.SettingsKind.*
import ru.sulgik.settings.core.SingleSettingSerializer
import ru.sulgik.settings.core.SingleSettingsDescriptor
import ru.sulgik.settings.core.SingleSettingsValue
import ru.sulgik.settings.provider.SettingsProvider
import kotlin.reflect.KType

class DataStoreSettingsProvider(
    private val context: Context,
    producers: List<SingleSettingSerializer<*>>,
) : SettingsProvider {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "preferences")

    private val producers = producers.associateBy { it.descriptor.type }

    override fun <T : Any> getSettingFlow(authScope: AuthScope, type: KType): Flow<T> {
        val producer = getSerializer<T>(type)
        return context.dataStore.data.map {
            val value = getValueFromPreferences(producer.descriptor, authScope, it)
            if (value != null) {
                producer.deserialize(SingleSettingsValue(value))
            } else {
                producer.descriptor.defaultValue
            }
        }
    }

    override suspend fun <T : Any> provide(authScope: AuthScope, value: T, type: KType) {
        val serializer = getSerializer<T>(type)
        val settingsValue = serializer.serialize(value)
        context.dataStore.edit {
            setValueToPreferences(serializer.descriptor, authScope, it, settingsValue)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T : Any> getSerializer(type: KType): SingleSettingSerializer<T> {
        val serializer = producers[type]
            ?: throw IllegalStateException("Descriptor for $type does not registered")
        return serializer as? SingleSettingSerializer<T>
            ?: throw IllegalStateException("Descriptor for $type does not match with type ${serializer::class}")
    }

    private fun <T : Any> getValueFromPreferences(
        descriptor: SingleSettingsDescriptor<T>,
        authScope: AuthScope,
        preferences: Preferences,
    ): Any? {
        val value = when (descriptor.kind) {
            STRING -> preferences[stringPreferencesKey(
                createPreferenceName(
                    authScope,
                    descriptor
                )
            )]

            BOOLEAN -> preferences[booleanPreferencesKey(
                createPreferenceName(
                    authScope,
                    descriptor
                )
            )]
        }
        return value
    }

    private fun <T : Any> setValueToPreferences(
        descriptor: SingleSettingsDescriptor<T>,
        authScope: AuthScope,
        preferences: MutablePreferences,
        value: SingleSettingsValue,
    ) {
        when (descriptor.kind) {
            STRING -> preferences[stringPreferencesKey(
                createPreferenceName(
                    authScope,
                    descriptor
                )
            )] =
                value.value as? String
                    ?: throw IllegalArgumentException("Unable to cast provided data $value to save with $descriptor")

            BOOLEAN -> preferences[booleanPreferencesKey(
                createPreferenceName(
                    authScope,
                    descriptor
                )
            )] =
                value.value as? Boolean
                    ?: throw IllegalArgumentException("Unable to cast provided data $value to save with $descriptor")

        }
    }

    private fun createPreferenceName(
        authScope: AuthScope,
        descriptor: SingleSettingsDescriptor<*>,
    ): String {
        return "${authScope.id}_${descriptor.name}"
    }

}