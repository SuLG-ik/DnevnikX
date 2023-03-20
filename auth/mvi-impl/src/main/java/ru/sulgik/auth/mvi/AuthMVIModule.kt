package ru.sulgik.auth.mvi

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

class AuthMVIModule {

    val module = module {
        factoryOf(::AuthStoreImpl) bind AuthStore::class
    }

}