package ru.sulgik.finalmarks.mvi

import com.arkivanov.mvikotlin.core.store.Store

interface FinalMarksStore :
    Store<FinalMarksStore.Intent, FinalMarksStore.State, FinalMarksStore.Label> {

    sealed interface Intent {

        object RefreshFinalMarks : Intent

    }

    data class State(
        val isLoading: Boolean = true,
        val isRefreshing: Boolean = false,
        val lessons: LessonsData? = null,
    ) {

        class LessonsData(
            val lessons: List<Lesson>,
        )

        class Lesson(
            val title: String,
            val marks: List<Mark>,
        )

        class Mark(
            val mark: String,
            val value: Int,
            val period: String,
        )

    }

    sealed interface Label

}