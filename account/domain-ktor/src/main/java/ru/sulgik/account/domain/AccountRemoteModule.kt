package ru.sulgik.account.domain

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

class AccountRemoteModule {

    val module = module {
        singleOf(::KtorRemoteAccountRepository) bind RemoteAccountRepository::class
    }

}