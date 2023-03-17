package ru.sulgik.diary.mvi

import com.arkivanov.mvikotlin.core.store.Store
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.datetime.LocalDate
import ru.sulgik.common.platform.DatePeriod
import ru.sulgik.common.platform.TimePeriod

interface DiaryStore : Store<DiaryStore.Intent, DiaryStore.State, DiaryStore.Label> {

    sealed interface Intent {
        data class SelectPeriodSelector(val period: DatePeriod) : Intent
        object HidePeriodSelector : Intent
        object SelectOtherPeriod : Intent
        object HideLessonInfo : Intent
        data class ShowLessonInfo(
            val date: State.DiaryDate, val lesson: State.Lesson,
        ) : Intent

        data class RefreshDiary(
            val period: DatePeriod,
        ) : Intent
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
            val periods: ImmutableList<DatePeriod>,
            val isOther: Boolean,
        )

        data class Diary(
            val data: ImmutableMap<DatePeriod, DiaryData>? = null,
            val selectedLesson: SelectedLesson? = null,
        )

        data class DiaryData(
            val isLoading: Boolean = true,
            val isRefreshing: Boolean = false,
            val diary: ImmutableList<DiaryDate>,
        )

        data class SelectedLesson(
            val date: DiaryDate, val lesson: Lesson,
        )

        data class DiaryDate(
            val date: LocalDate,
            val alert: DiaryAlert?,
            val lessons: ImmutableList<Lesson>,
        )

        data class DiaryAlert(
            val isOverload: Boolean,
            val message: String,
        )

        data class Lesson(
            val number: String,
            val title: String,
            val time: TimePeriod,
            val homework: ImmutableList<Homework>,
            val files: ImmutableList<File>,
            val marks: ImmutableList<Mark>,
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