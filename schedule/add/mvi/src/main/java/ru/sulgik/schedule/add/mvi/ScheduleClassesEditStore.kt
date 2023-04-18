package ru.sulgik.schedule.add.mvi

import com.arkivanov.mvikotlin.core.store.Store

interface ScheduleClassesEditStore :
    Store<ScheduleClassesEditStore.Intent, ScheduleClassesEditStore.State, ScheduleClassesEditStore.Label> {

    sealed interface Intent {

        object AddClass : Intent

        data class DeleteClass(
            val number: String,
            val group: String,
        ) : Intent

        data class SelectNumber(val number: String) : Intent

        data class SelectGroup(val group: String) : Intent

    }

    data class State(
        val isRefreshing: Boolean = false,
        val classSelector: ClassSelector = ClassSelector(),
        val savedClasses: SavedClasses = SavedClasses()
    ) {

        data class ClassSelector(
            val isAvailable: Boolean = false,
            val data: ClassSelectorData = ClassSelectorData(),
        )

        data class ClassSelectorData(
            val selectedNumber: String? = null,
            val selectedGroup: String? = null,
        )

        data class SavedClasses(
            val isLoading: Boolean = true,
            val data: SavedClassesData? = null,
        )

        data class SavedClassesData(
            val classes: List<ClassData>,
        )


        data class ClassData(
            val fullTitle: String,
            val number: String,
            val group: String,
            val isPermanent: Boolean,
        )
    }

    sealed interface Label

}