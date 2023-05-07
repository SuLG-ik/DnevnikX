package ru.sulgik.images.impl

import coil.request.Options
import ru.sulgik.images.StorageImage

class StorageReferenceKeyer : BaseKeyer<StorageImage>("storage") {
    override fun getKey(data: StorageImage, options: Options): String = data.path

}