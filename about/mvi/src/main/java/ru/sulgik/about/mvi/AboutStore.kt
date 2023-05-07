package ru.sulgik.about.mvi

import com.arkivanov.mvikotlin.core.store.Store
import ru.sulgik.images.RemoteImage

interface AboutStore : Store<AboutStore.Intent, AboutStore.State, AboutStore.Label> {

    sealed interface Intent

    data class State(val data: AboutData? = null) {
        data class AboutData(
            val application: ApplicationData,
            val domain: DomainInfo,
            val developer: DeveloperData,
        ) {
            class ApplicationData(
                val name: String,
                val version: String,
            )

            class DomainInfo(
                val name: String,
                val domain: String,
                val uri: String,
                val logo: RemoteImage,
            )

            class DeveloperData(
                val name: String,
                val uri: String,
            )
        }
    }

    sealed interface Label

}