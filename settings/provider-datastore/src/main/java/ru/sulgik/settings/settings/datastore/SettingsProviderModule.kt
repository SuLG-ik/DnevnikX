package ru.sulgik.settings.settings.datastore

import org.koin.dsl.bind
import org.koin.dsl.module
import ru.sulgik.settings.provider.SettingsProvider

class SettingsProviderModule {

    val module = module {
        single {
            DataStoreSettingsProvider(get(), getAll())
        } bind SettingsProvider::class
    }

}

