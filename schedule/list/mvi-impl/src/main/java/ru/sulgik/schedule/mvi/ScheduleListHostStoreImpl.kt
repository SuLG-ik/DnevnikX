package ru.sulgik.schedule.mvi

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.utils.ExperimentalMviKotlinApi
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineExecutorFactory
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import ru.sulgik.account.domain.CachedAccountDataRepository
import ru.sulgik.account.domain.data.AccountData
import ru.sulgik.auth.core.AuthScope
import ru.sulgik.common.platform.DatePeriod
import ru.sulgik.core.syncDispatch
import ru.sulgik.kacher.core.on
import ru.sulgik.schedule.add.domain.CachedScheduleClassRepository
import ru.sulgik.schedule.add.domain.data.GetScheduleClassesOutput

@OptIn(ExperimentalMviKotlinApi::class)
class ScheduleListHostStoreImpl(
    storeFactory: StoreFactory,
    coroutineDispatcher: CoroutineDispatcher,
    authScope: AuthScope,
    cachedScheduleClassRepository: CachedScheduleClassRepository,
    cachedAccountDataRepository: CachedAccountDataRepository,
) : ScheduleListHostStore,
    Store<ScheduleListHostStore.Intent, ScheduleListHostStore.State, ScheduleListHostStore.Label> by storeFactory.create<_, Action, Message, _, _>(
        name = "ScheduleListHostStoreImpl",
        initialState = ScheduleListHostStore.State(),
        bootstrapper = coroutineBootstrapper(coroutineDispatcher) {
            dispatch(Action.Setup)
        },
        executorFactory = coroutineExecutorFactory(coroutineDispatcher) {
            onAction<Action.Setup> {
                launch(Dispatchers.Main) {
                    cachedAccountDataRepository.getData(authScope).on { accountResponse ->
                        cachedScheduleClassRepository.getClasses(
                            auth = authScope,
                        ).on { classesResponse ->
                            val accountData = accountResponse.data
                            val data = classesResponse.data
                            if (data != null) {
                                val accountClasses = accountData?.classes.orEmpty()
                                    .map { it.toState() }
                                val accountClassesTitles = accountClasses.map { it.fullTitle }
                                syncDispatch(
                                    Message.UpdateClasses(
                                        schedule = (
                                                accountClasses + data.classes
                                                    .filter { it.fullTitle !in accountClassesTitles }
                                                    .map { it.toState() }
                                                )
                                            .toPersistentList()
                                    )
                                )
                            }
                        }
                    }
                }
            }
            onIntent<ScheduleListHostStore.Intent.SelectClass> {
                if (it.classData == state.savedClasses.data?.selectedClass) {
                    return@onIntent
                }
                dispatch(Message.SelectClass(it.classData))
            }
        },
        reducer = {
            when (it) {
                is Message.UpdateClasses -> {
                    val savedClassesData = savedClasses.data
                    copy(
                        savedClasses = savedClasses.copy(
                            isLoading = false,
                            data = savedClassesData?.copy(
                                selectedClass = if (savedClassesData.selectedClass in it.schedule) savedClassesData.selectedClass else it.schedule.first(),
                                classes = it.schedule,
                            ) ?: ScheduleListHostStore.State.SavedClassesData(
                                selectedClass = it.schedule.first(),
                                classes = it.schedule,
                            )
                        ),
                    )
                }

                is Message.SelectClass -> {
                    val savedClassesData = savedClasses.data
                    copy(
                        savedClasses = savedClasses.copy(
                            isLoading = false,
                            data = savedClassesData?.copy(
                                selectedClass = if (it.classData in savedClassesData.classes) it.classData else savedClassesData.classes.first(),
                            )
                        ),
                    )
                }
            }
        },
    ) {

    private sealed interface Message {

        data class UpdateClasses(
            val schedule: ImmutableList<ScheduleListHostStore.State.ClassData>,
        ) : Message

        data class SelectClass(
            val classData: ScheduleListHostStore.State.ClassData,
        ) : Message

    }

    private sealed interface Action {
        object Setup : Action
    }

}

private fun GetScheduleClassesOutput.Class.toState(): ScheduleListHostStore.State.ClassData {
    return ScheduleListHostStore.State.ClassData(
        fullTitle = fullTitle,
        number = number,
        group = group,
    )
}

private fun AccountData.Class.toState(): ScheduleListHostStore.State.ClassData {
    val (number, group) = fullTitle.separateNumberAndGroup()
    return ScheduleListHostStore.State.ClassData(
        fullTitle = fullTitle,
        number = number,
        group = group,
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


private fun String.formatTeacherName(): String {
    if (isBlank()) {
        return this
    }
    val words = split(" ")
    if (words.size == 1) {
        return this
    }
    return buildString {
        append(words[0])
        append(" ")
        for (i in 1 until words.size) {
            append(words[i].first())
            append(". ")
        }
    }
}

fun getPeriod(date: kotlinx.datetime.LocalDate): DatePeriod {
    val start = date.minus((date.dayOfWeek.value - 1).toLong(), DateTimeUnit.DAY)
    return DatePeriod(start, start.plus(6, DateTimeUnit.DAY))
}