package ru.sulgik.marksedit.mvi

import kotlinx.collections.immutable.PersistentList
import kotlinx.datetime.LocalDate
import ru.sulgik.common.platform.DatePeriod
import ru.sulgik.core.ParametrizedStore

interface MarksEditStore :
    ParametrizedStore<MarksEditStore.Intent, MarksEditStore.State, MarksEditStore.Label, MarksEditStore.Params> {

    data class Params(
        val period: Period,
        val title: String,
    ) {
        data class Period(
            val title: String,
            val period: DatePeriod,
        )
    }

    sealed interface Intent {

        data class AddMark(val value: Int) : Intent
        data class ChangeStatus(val index: Int) : Intent

        object Clear : Intent

    }

    data class State(
        val lessonData: LessonData = LessonData(),
    ) {

        data class LessonData(
            val isLoading: Boolean = true,
            val period: Period? = null,
            val data: Lesson? = null,
        )

        data class Period(
            val title: String,
        )

        data class Lesson(
            val title: String,
            val average: String,
            val averageValue: Int,
            val marks: PersistentList<Mark>,
        )

        data class Mark(
            val status: MarkStatus,
            val mark: String,
            val value: Int,
            val date: LocalDate?,
        )

        enum class MarkStatus {
            DISABLED, ENABLED,
        }

    }

    sealed interface Label

}