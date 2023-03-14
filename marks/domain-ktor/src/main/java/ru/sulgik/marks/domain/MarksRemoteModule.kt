package ru.sulgik.marks.domain

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

class MarksRemoteModule {

    val module = module {
        singleOf(::KtorRemoteMarksRepository) bind RemoteMarksRepository::class
    }

}