package ru.sulgik.diary.mvi

import com.arkivanov.mvikotlin.core.store.Store

interface DiarySettingsStore :
    Store<DiarySettingsStore.Intent, DiarySettingsStore.State, DiarySettingsStore.Label> {

    sealed interface Intent

    data class State(
        val settings: DiarySettings = DiarySettings(),
    ) {
        data class DiarySettings(
            val isPagerEnabled: Boolean = false,
        )

    }

    sealed interface Label

}