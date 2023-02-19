package ru.sulgik.dnevnikx.mvi.schedule

import com.arkivanov.mvikotlin.core.store.Store
import kotlinx.datetime.LocalDate
import ru.sulgik.dnevnikx.platform.DatePeriod
import ru.sulgik.dnevnikx.platform.TimePeriod

interface ScheduleStore: Store<ScheduleStore.Intent, ScheduleStore.State, ScheduleStore.Label> {

    sealed interface Intent {
        data class SelectPeriod(val period: DatePeriod) : Intent
        object SelectOtherPeriod : Intent
    }

    data class State(
        val isRefreshing: Boolean = false,
        val periods: Periods = Periods(),
        val schedule: Schedule = Schedule(),
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

        data class Schedule(
            val isLoading: Boolean = true,
            val schedule: ScheduleData? = null,
        )

        data class ScheduleData(
            val schedule: List<ScheduleDate>,
        )

        data class ScheduleDate(
            val title: String,
            val date: LocalDate,
            val lessonGroups: List<LessonGroup>,
        )

        data class LessonGroup(
            val number: String,
            val lessons: List<Lesson>,
        )

        data class Lesson(
            val title: String,
            val time: TimePeriod,
            val teacher: String,
            val group: String? = null,
        )



    }
    sealed interface Label

}