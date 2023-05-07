package ru.sulgik.images.domain

import ru.sulgik.images.RemoteImage
import ru.sulgik.images.StorageImage

class ImageEmbedded(
    val path: String,
    val source: ImageType,
)

enum class ImageType {
    STORAGE
}

fun ImageEmbedded.toData(): StorageImage {
    return when (source) {
        ImageType.STORAGE -> StorageImage(path)
    }
}

fun RemoteImage.toDomain(): ImageEmbedded {
    return when (this) {
        is StorageImage -> ImageEmbedded(path, ImageType.STORAGE)
    }
}