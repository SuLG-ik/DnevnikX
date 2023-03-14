package ru.sulgik.main.mvi

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

class MainMVIModule {

    val module = module {
        factoryOf(::MainStoreImpl) bind MainStore::class
        factory {
            MainWithSplashStoreImpl(
                storeFactory = get(),
                coroutineDispatcher = get(),
                savedState = getOrNull(),
                sessionAccountRepository = get(),
                localAuthRepository = get()
            )
        } bind MainWithSplashStore::class
    }

}