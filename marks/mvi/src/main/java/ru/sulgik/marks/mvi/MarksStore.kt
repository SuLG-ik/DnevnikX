package ru.sulgik.marks.mvi

import com.arkivanov.mvikotlin.core.store.Store
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.datetime.LocalDate
import ru.sulgik.common.platform.DatePeriod

interface MarksStore : Store<MarksStore.Intent, MarksStore.State, MarksStore.Label> {

    sealed interface Intent {

        data class SelectPeriod(val period: State.Period) : Intent

        data class SelectMark(val mark: Pair<State.Lesson, State.Mark>) : Intent

        object HideMark : Intent

        data class RefreshMarks(val period: State.Period) : Intent
    }

    data class State(
        val isRefreshing: Boolean = false,
        val periods: Periods = Periods(),
        val marks: Marks = Marks(),
    ) {
        data class Periods(
            val isLoading: Boolean = true,
            val data: PeriodsData? = null,
        )

        data class PeriodsData(
            val selectedPeriod: Period,
            val periods: List<Period>,
        )

        data class Period(
            val title: String,
            val period: DatePeriod,
        )

        data class Marks(
            val selectedMark: SelectedMark? = null,
            val data: ImmutableMap<Period, MarksData> = persistentMapOf(),
        )

        class SelectedMark(
            val lesson: Lesson,
            val mark: Mark,
        )

        data class MarksData(
            val isLoading: Boolean = true,
            val isRefreshing: Boolean = false,
            val lessons: List<Lesson>,
        )

        data class Lesson(
            val title: String,
            val average: String,
            val averageValue: Int,
            val marks: List<Mark>,
        )

        data class Mark(
            val mark: String,
            val value: Int,
            val date: LocalDate,
            val message: String?,
        )
    }

    sealed interface Label

}