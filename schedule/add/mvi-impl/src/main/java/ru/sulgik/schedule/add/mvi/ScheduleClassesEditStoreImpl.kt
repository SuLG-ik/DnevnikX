package ru.sulgik.schedule.add.mvi

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.utils.ExperimentalMviKotlinApi
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineExecutorFactory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.sulgik.account.domain.CachedAccountDataRepository
import ru.sulgik.account.domain.data.AccountData
import ru.sulgik.auth.core.AuthScope
import ru.sulgik.kacher.core.on
import ru.sulgik.schedule.add.domain.CachedScheduleClassRepository
import ru.sulgik.schedule.add.domain.data.GetScheduleClassesOutput

@OptIn(ExperimentalMviKotlinApi::class)
class ScheduleClassesEditStoreImpl(
    storeFactory: StoreFactory,
    coroutineDispatcher: CoroutineDispatcher,
    authScope: AuthScope,
    cachedScheduleClassRepository: CachedScheduleClassRepository,
    cachedAccountDataRepository: CachedAccountDataRepository,
) : ScheduleClassesEditStore,
    Store<ScheduleClassesEditStore.Intent, ScheduleClassesEditStore.State, ScheduleClassesEditStore.Label> by storeFactory.create<_, Action, Message, _, _>(
        name = "ScheduleClassesEditStoreImpl",
        initialState = ScheduleClassesEditStore.State(),
        bootstrapper = coroutineBootstrapper(coroutineDispatcher) {
            dispatch(Action.Setup)
        },
        executorFactory = coroutineExecutorFactory(coroutineDispatcher) {
            onAction<Action.Setup> {
                launch(Dispatchers.Main) {
                    cachedAccountDataRepository.getData(authScope).on { accountResponse ->
                        cachedScheduleClassRepository.getClasses(authScope).on { classesResponse ->
                            dispatch(
                                Message.SetData(
                                    ScheduleClassesEditStore.State.SavedClassesData(
                                        accountResponse.data?.classes.orEmpty()
                                            .map { it.toState() } + classesResponse.data?.classes.orEmpty()
                                            .map { it.toState() }
                                    )
                                )
                            )
                        }
                    }
                }
            }
            onIntent<ScheduleClassesEditStore.Intent.AddClass> {
                val classSelector = state.classSelector.data
                val selectedNumber = classSelector.selectedNumber
                val selectedGroup = classSelector.selectedGroup
                if (selectedNumber == null || selectedGroup == null || !state.savedClasses.data.isAvailable(
                        selectedNumber,
                        selectedGroup
                    )
                ) {
                    return@onIntent
                }
                launch {
                    cachedScheduleClassRepository.addClass(
                        authScope,
                        selectedNumber,
                        selectedGroup
                    )
                }
            }
            onIntent<ScheduleClassesEditStore.Intent.SelectGroup> {
                dispatch(Message.SetGroup(it.group))
            }
            onIntent<ScheduleClassesEditStore.Intent.SelectNumber> {
                dispatch(Message.SetNumber(it.number))
            }
            onIntent<ScheduleClassesEditStore.Intent.DeleteClass> {
                launch {
                    cachedScheduleClassRepository.deleteClass(
                        auth = authScope,
                        number = it.number,
                        group = it.group
                    )
                }
            }
        },
        reducer = {
            when (it) {
                is Message.SetData -> copy(
                    classSelector = classSelector.copy(
                        isAvailable = it.classes.isAvailable(
                            number = classSelector.data.selectedNumber,
                            group = classSelector.data.selectedGroup
                        )
                    ),
                    savedClasses = ScheduleClassesEditStore.State.SavedClasses(
                        isLoading = false,
                        data = it.classes,
                    )
                )

                is Message.SetGroup -> copy(
                    classSelector = classSelector.copy(
                        isAvailable = savedClasses.data.isAvailable(
                            number = classSelector.data.selectedNumber,
                            group = it.group,
                        ),
                        data = classSelector.data.copy(selectedGroup = it.group)
                    )
                )

                is Message.SetNumber -> copy(
                    classSelector = classSelector.copy(
                        isAvailable = savedClasses.data.isAvailable(
                            number = it.number,
                            group = classSelector.data.selectedGroup
                        ),
                        data = classSelector.data.copy(selectedNumber = it.number)
                    )
                )

            }
        },
    ) {

    private sealed interface Message {
        data class SetData(
            val classes: ScheduleClassesEditStore.State.SavedClassesData,
        ) : Message

        data class SetNumber(val number: String) : Message

        data class SetGroup(val group: String) : Message
    }

    private sealed interface Action {
        object Setup : Action

    }

}

private fun ScheduleClassesEditStore.State.SavedClassesData?.isAvailable(
    number: String?,
    group: String?
): Boolean {
    if (number == null || group == null || this == null)
        return false
    return classes.none { it.number == number && it.group == group }
}

private fun GetScheduleClassesOutput.Class.toState(): ScheduleClassesEditStore.State.ClassData {
    return ScheduleClassesEditStore.State.ClassData(
        fullTitle = fullTitle, number = number, group = group, isPermanent = false,
    )
}

private fun AccountData.Class.toState(): ScheduleClassesEditStore.State.ClassData {
    val (number, group) = fullTitle.separateNumberAndGroup()
    return ScheduleClassesEditStore.State.ClassData(
        fullTitle = fullTitle,
        number = number,
        group = group,
        isPermanent = true,
    )
}

private fun String.separateNumberAndGroup(): Pair<String, String> {
    val number = StringBuilder()
    val group = StringBuilder()
    var isGroup = false
    forEach {
        when {
            isGroup -> group.append(it)
            !it.isDigit() -> {
                group.append(it)
                isGroup = true
            }

            else -> number.append(it)
        }
    }
    return number.toString() to group.toString()
}
