package ru.sulgik.images.impl

import coil.key.Keyer
import coil.request.Options

abstract class BaseKeyer<T : Any>(
    prefix: String,
) : Keyer<T> {

    private val prefix = "${prefix}_"

    final override fun key(data: T, options: Options): String? {
        val key = getKey(data, options) ?: return null
        return prefix + key
    }

    abstract fun getKey(data: T, options: Options): String?
}