package ru.sulgik.account.domain

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

class AccountSessionLocalModule {

    val module = module {
        singleOf(::DatastoreLocalSessionAccountRepository) bind LocalSessionAccountRepository::class
    }

}