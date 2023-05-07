package ru.sulgik.auth.domain

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

class AuthMergedModule {

    val module = module {
        singleOf(::RoomMergedAuthRepository) bind MergedAuthRepository::class
        singleOf(::CachedMergedVendorAuthRepository) bind MergedVendorAuthRepository::class
    }

}