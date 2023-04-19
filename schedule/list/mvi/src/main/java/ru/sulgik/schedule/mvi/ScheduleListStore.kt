package ru.sulgik.schedule.mvi

import com.arkivanov.mvikotlin.core.store.Store
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.datetime.LocalDate
import ru.sulgik.common.platform.TimePeriod

interface ScheduleListStore :
    Store<ScheduleListStore.Intent, ScheduleListStore.State, ScheduleListStore.Label> {

    data class Params(
        val classFullTitle: String,
    )

    sealed interface Intent {

        object RefreshSchedule : Intent

        data class SelectClass(
            val classFullTitle: String,
        ) : Intent

    }

    data class State(
        val isRefreshing: Boolean = false,
        val schedule: Schedule = Schedule(),
    ) {
        data class Schedule(
            val data: ScheduleData = ScheduleData(),
        )

        data class SelectedClass(
            val fullTitle: String,
        )

        data class ScheduleData(
            val isLoading: Boolean = true,
            val isRefreshing: Boolean = false,
            val selectedClass: SelectedClass? = null,
            val schedule: ImmutableList<ScheduleDate> = persistentListOf(),
        )

        data class ScheduleDate(
            val date: LocalDate,
            val lessonGroups: ImmutableList<LessonGroup>,
        )

        data class LessonGroup(
            val number: String,
            val lessons: ImmutableList<Lesson>,
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