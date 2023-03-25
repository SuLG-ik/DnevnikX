package ru.sulgik.marksupdates.mvi

import com.arkivanov.mvikotlin.core.store.Store
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.datetime.LocalDate


interface MarksUpdatesStore :
    Store<MarksUpdatesStore.Intent, MarksUpdatesStore.State, MarksUpdatesStore.Label> {

    sealed interface Intent {

        object LoadNextPage : Intent
        object Refresh : Intent

    }

    data class State(
        val updates: MarksUpdates = MarksUpdates(),
    ) {

        data class MarksUpdates(
            val isLoading: Boolean = true,
            val data: MarksUpdatesData? = MarksUpdatesData(),
        )

        data class MarksUpdatesData(
            val isRefreshing: Boolean = false,
            val latest: MarksUpdatesPeriodData = MarksUpdatesPeriodData(),
            val old: MarksUpdatesPeriodData = MarksUpdatesPeriodData(),
        )

        data class MarksUpdatesPeriodData(
            val isNextPageLoading: Boolean = true,
            val data: ImmutableList<MarkUpdate> = persistentListOf(),
        )

        data class MarkUpdate(
            val lesson: Lesson,
            val current: Mark,
            val previous: Mark?,
        )

        data class Lesson(
            val name: String,
            val date: LocalDate,
        )

        data class Mark(
            val mark: String,
            val value: Int,
        )

        companion object

    }

    sealed interface Label

}