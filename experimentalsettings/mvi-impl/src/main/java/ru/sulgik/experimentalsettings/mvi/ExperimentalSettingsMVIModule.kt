package ru.sulgik.experimentalsettings.mvi

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

class ExperimentalSettingsMVIModule {

    val module = module {
        factoryOf(::ExperimentalSettingsStoreImpl) bind ExperimentalSettingsStore::class
    }

}
