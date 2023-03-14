package ru.sulgik.account.mvi

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

class AccountMVIModule {

    val module = module {
        factoryOf(::AccountStoreImpl) bind AccountStore::class
    }

}