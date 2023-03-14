package ru.sulgik.auth.ktor

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

class AuthClientKtorModule {

    val module = module {
        singleOf(::Client)
    }

}