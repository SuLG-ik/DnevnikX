package ru.sulgik.about.domain

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import ru.sulgik.about.domain.data.BuiltInAboutRepository

class AboutBuiltInModule {

    val module = module {
        singleOf(::BuildConfigBuiltInAboutRepository) bind BuiltInAboutRepository::class
    }

}