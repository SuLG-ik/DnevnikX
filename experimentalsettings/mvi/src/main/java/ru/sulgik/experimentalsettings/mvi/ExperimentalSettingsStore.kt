package ru.sulgik.experimentalsettings.mvi

import com.arkivanov.mvikotlin.core.store.Store

interface ExperimentalSettingsStore :
    Store<ExperimentalSettingsStore.Intent, ExperimentalSettingsStore.State, ExperimentalSettingsStore.Label> {

    sealed interface Intent {

        class ToggleNestedScreenTransition(val value: Boolean) : Intent

    }

    data class State(
        val isLoading: Boolean = true,
        val settings: SettingsData? = null,
    ) {

        data class SettingsData(
            val ui: UISettings,
        )

        data class UISettings(
            val isNestedScreenTransitionEnabled: Boolean = false,
        )

    }

    sealed interface Label

}