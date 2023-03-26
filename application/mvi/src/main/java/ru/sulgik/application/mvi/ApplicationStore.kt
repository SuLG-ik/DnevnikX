package ru.sulgik.application.mvi

import com.arkivanov.mvikotlin.core.store.Store

interface ApplicationStore :
    Store<ApplicationStore.Intent, ApplicationStore.State, ApplicationStore.Label> {

    sealed interface Intent

    data class State(
        val applicationConfig: ApplicationConfig = ApplicationConfig(),
    ) {
        class ApplicationConfig
    }

    sealed interface Label


}