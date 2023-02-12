package ru.sulgik.dnevnikx.mvi.diary

import com.arkivanov.mvikotlin.core.store.Store
import kotlinx.datetime.LocalDate
import ru.sulgik.dnevnikx.repository.data.DatePeriod
import ru.sulgik.dnevnikx.repository.data.TimePeriod

interface DiaryStore : Store<DiaryStore.Intent, DiaryStore.State, DiaryStore.Label> {

    sealed interface Intent {
        data class SelectPeriod(val period: DatePeriod) : Intent
        object SelectOtherPeriod : Intent
    }

    data class State(
        val isRefreshing: Boolean = false,
        val periods: Periods = Periods(),
        val diary: Diary = Diary(),
    ) {
        data class Periods(
            val isLoading: Boolean = true,
            val data: PeriodsData? = null,
        )

        data class PeriodsData(
            val selectedPeriod: DatePeriod,
            val currentPeriod: DatePeriod?,
            val nextPeriod: DatePeriod?,
            val previousPeriod: DatePeriod?,
            val periods: List<DatePeriod>,
            val isOther: Boolean,
        )

        data class Diary(
            val isLoading: Boolean = true,
            val data: DiaryData? = null,
        )

        data class DiaryData(
            val diary: List<DiaryDate>,
        )

        data class DiaryDate(
            val date: LocalDate,
            val lessons: List<Lesson>,
        )

        data class Lesson(
            val number: String,
            val title: String,
            val time: TimePeriod,
            val homework: List<Homework>,
            val files: List<File>,
            val marks: List<Mark>,
        )

        data class Homework(
            val text: String,
        )


        data class File(
            val name: String,
            val url: String,
        )

        data class Mark(
            val mark: String,
            val value: Int,
        )

    }

    sealed interface Label

}