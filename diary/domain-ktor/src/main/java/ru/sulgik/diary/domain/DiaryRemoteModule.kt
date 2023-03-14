package ru.sulgik.diary.domain

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

class DiaryRemoteModule {

    val module = module {
        singleOf(::KtorRemoteDiaryRepository) bind RemoteDiaryRepository::class
    }

}