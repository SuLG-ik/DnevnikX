package ru.sulgik.images.impl

import coil.ImageLoader
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module


class ImagesLoaderModule {

    val module = module {
        singleOf(::MultipleImageLoader) bind ImageLoader::class
    }

}