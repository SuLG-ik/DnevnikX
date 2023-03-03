package ru.sulgik.dnevnikx.mvi.marks

import com.arkivanov.mvikotlin.core.store.Store
import kotlinx.datetime.LocalDate
import ru.sulgik.dnevnikx.platform.DatePeriod

interface MarksStore : Store<MarksStore.Intent, MarksStore.State, MarksStore.Label> {

    sealed interface Intent {

        data class SelectPeriod(val period: State.Period) : Intent

        data class SelectMark(val mark: Pair<State.Lesson, State.Mark>) : Intent

        object HideMark : Intent

        object RefreshMarks : Intent
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
            val isLoading: Boolean = true,
            val isRefreshing: Boolean = false,
            val data: MarksData? = null,
        )

        data class MarksData(
            val selectedMark: Pair<Lesson, Mark>?,
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