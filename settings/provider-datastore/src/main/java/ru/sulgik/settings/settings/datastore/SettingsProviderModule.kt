package ru.sulgik.settings.settings.datastore

import org.koin.core.annotation.KoinInternalApi
import org.koin.core.definition.Kind
import org.koin.core.scope.Scope
import org.koin.dsl.bind
import org.koin.dsl.module
import ru.sulgik.settings.provider.SettingsProvider
import kotlin.reflect.full.isSubclassOf

class SettingsProviderModule {

    val module = module {
        single {
            DataStoreSettingsProvider(get(), getAllCustom())
        } bind SettingsProvider::class
    }

}


@OptIn(KoinInternalApi::class)
inline fun <reified T : Any> Scope.getAllCustom(): List<T> =
    getKoin().let { koin ->
        koin.instanceRegistry.instances.map { it.value.beanDefinition }
            .filter { it.kind == Kind.Singleton }
            .filter { it.primaryType.isSubclassOf(T::class) }
            .map { koin.get(clazz = it.primaryType, qualifier = null, parameters = null) }
    }