package ru.sulgik.auth.domain

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

class VendorAuthRemoteModule {

    val module = module {
        singleOf(::FirestoreRemoteVendorAuthRepository) bind RemoteVendorAuthRepository::class
    }

}