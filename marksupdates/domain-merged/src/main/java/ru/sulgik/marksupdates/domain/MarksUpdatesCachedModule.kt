package ru.sulgik.marksupdates.domain

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

class MarksUpdatesCachedModule {

    val module = module {
        factoryOf(::AndroidPagingRemoteMarksUpdateSource) bind PagingRemoteMarksUpdateSource::class
    }

}