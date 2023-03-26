package ru.sulgik.experimentalsettings.mvi

import com.arkivanov.mvikotlin.core.store.Store

interface ExperimentalSettingsStore :
    Store<ExperimentalSettingsStore.Intent, ExperimentalSettingsStore.State, ExperimentalSettingsStore.Label> {

    sealed interface Intent

    data class State(
        val isLoading: Boolean = true,
        val settings: SettingsData? = null,
    ) {

        class SettingsData
    }

    sealed interface Label

}