package ru.sulgik.auth.mvi

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module
import ru.sulgik.dnevnikx.mvi.auth.AuthStore

class AuthMVIModule {

    val module = module {
        factoryOf(::AuthStoreImpl) bind AuthStore::class
    }

}