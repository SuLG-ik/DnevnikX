package ru.sulgik.finalmarks.domain

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

class FinalMarksRemoteModule {

    val module = module {
        singleOf(::KtorRemoteFinalMarksRepository) bind RemoteFinalMarksRepository::class
    }

}