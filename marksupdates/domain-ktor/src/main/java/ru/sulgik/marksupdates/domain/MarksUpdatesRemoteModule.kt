package ru.sulgik.marksupdates.domain

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

class MarksUpdatesRemoteModule {

    val module = module {
        singleOf(::KtorRemoteMarksUpdatesRepository) bind RemoteMarksUpdatesRepository::class
    }

}