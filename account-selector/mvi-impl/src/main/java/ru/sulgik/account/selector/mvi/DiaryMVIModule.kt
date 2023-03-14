package ru.sulgik.account.selector.mvi

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

class AccountSelectorMVIModule {

    val module = module {
        factoryOf(::AccountSelectorStoreImpl) bind AccountSelectorStore::class
    }

}