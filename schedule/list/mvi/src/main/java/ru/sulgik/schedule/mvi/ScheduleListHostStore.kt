package ru.sulgik.schedule.mvi

import com.arkivanov.mvikotlin.core.store.Store
import kotlinx.collections.immutable.ImmutableList

interface ScheduleListHostStore :
    Store<ScheduleListHostStore.Intent, ScheduleListHostStore.State, ScheduleListHostStore.Label> {

    sealed interface Intent {
        data class SelectClass(val classData: State.ClassData) : Intent
    }

    data class State(
        val savedClasses: SavedClasses = SavedClasses(),
    ) {

        data class SavedClasses(
            val isLoading: Boolean = true,
            val data: SavedClassesData? = null,
        )

        data class SavedClassesData(
            val selectedClass: ClassData,
            val classes: ImmutableList<ClassData>,
        )


        data class ClassData(
            val fullTitle: String,
            val number: String,
            val group: String,
        )

    }

    sealed interface Label

}