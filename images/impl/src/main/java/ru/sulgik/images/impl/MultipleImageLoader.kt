package ru.sulgik.images.impl

import android.content.Context
import coil.ImageLoader
import coil.util.DebugLogger
import com.google.firebase.storage.FirebaseStorage

class MultipleImageLoader(
    context: Context,
    storage: FirebaseStorage
) : ImageLoader by buildDefaultImageLoader(context, storage) {
    companion object {

        @JvmStatic
        private fun buildDefaultImageLoader(
            context: Context,
            storage: FirebaseStorage
        ): ImageLoader {
            val loader = ImageLoader.Builder(context)
                .logger(DebugLogger())
                .components {
                    add(StorageReferenceKeyer())
                    add(StorageReferenceFetcher.Factory(storage))
                }.build()
            return loader
        }

    }
}