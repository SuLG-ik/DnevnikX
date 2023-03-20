package ru.sulgik.finalmarks.mvi

import com.arkivanov.mvikotlin.core.store.Store
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

interface FinalMarksStore :
    Store<FinalMarksStore.Intent, FinalMarksStore.State, FinalMarksStore.Label> {

    sealed interface Intent {

        object RefreshFinalMarks : Intent

    }

    data class State(
        val isLoading: Boolean = true,
        val lessons: LessonsData? = null,
    ) {

        data class LessonsData(
            val isRefreshing: Boolean = false,
            val lessons: ImmutableList<Lesson> = persistentListOf(),
        )

        data class Lesson(
            val title: String,
            val marks: ImmutableList<Mark>,
        )

        data class Mark(
            val mark: String,
            val value: Int,
            val period: String,
        )

    }

    sealed interface Label

}