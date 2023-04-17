package ru.sulgik.account.domain

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

class AccountCachedModule {

    val module = module {
        singleOf(::MergedCachedAccountDataRepository) bind CachedAccountDataRepository::class
    }

}