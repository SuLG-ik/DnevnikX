package ru.sulgik.marks.mvi

import com.arkivanov.mvikotlin.core.store.Store

interface MarksSettingsStore :
    Store<MarksSettingsStore.Intent, MarksSettingsStore.State, MarksSettingsStore.Label> {

    sealed interface Intent

    data class State(
        val settings: MarksSettings = MarksSettings(),
    ) {
        data class MarksSettings(
            val isPagerEnabled: Boolean = false,
        )

    }

    sealed interface Label

}