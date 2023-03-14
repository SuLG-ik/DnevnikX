package ru.sulgik.account.domain

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

class AccountLocalModule {

    val module = module {
        singleOf(::RoomLocalAccountDataRepository) bind LocalAccountDataRepository::class
        singleOf(::RoomLocalAccountRepository) bind LocalAccountRepository::class
    }

}