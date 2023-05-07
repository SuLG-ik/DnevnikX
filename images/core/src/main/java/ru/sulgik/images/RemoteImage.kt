package ru.sulgik.images

sealed class RemoteImage

data class StorageImage(
    val path: String,
) : RemoteImage()