package ru.sulgik.schedule.mvi

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.utils.ExperimentalMviKotlinApi
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineExecutorFactory
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ru.sulgik.auth.core.AuthScope
import ru.sulgik.core.syncDispatch
import ru.sulgik.schedule.domain.RemoteScheduleRepository
import ru.sulgik.schedule.domain.data.GetScheduleOutput

@OptIn(ExperimentalMviKotlinApi::class)
class ScheduleListStoreImpl(
    storeFactory: StoreFactory,
    coroutineDispatcher: CoroutineDispatcher,
    authScope: AuthScope,
    mergedScheduleRepository: RemoteScheduleRepository,
) : ScheduleListStore,
    Store<ScheduleListStore.Intent, ScheduleListStore.State, ScheduleListStore.Label> by storeFactory.create<_, Action, Message, _, _>(
        name = "ScheduleStoreListImpl",
        initialState = ScheduleListStore.State(),
        bootstrapper = coroutineBootstrapper(coroutineDispatcher) {
            dispatch(Action.Setup)
        },
        executorFactory = coroutineExecutorFactory(coroutineDispatcher) {
            var classLoadingJob: Job? = null
            onIntent<ScheduleListStore.Intent.SelectClass> {
                if (state.schedule.data.selectedClass?.fullTitle == it.classFullTitle) {
                    return@onIntent
                }
                dispatch(Message.SetLoading(it.classFullTitle))
                val job = classLoadingJob
                classLoadingJob = launch(Dispatchers.Main) {
                    job?.cancel()
                    val schedule = mergedScheduleRepository.getSchedule(
                        auth = authScope,
                        classGroup = it.classFullTitle,
                    ).toState()
                    syncDispatch(
                        Message.UpdateSchedule(
                            classFullTitle = it.classFullTitle,
                            schedule = schedule
                        )
                    )
                }
            }
            onIntent<ScheduleListStore.Intent.RefreshSchedule> {
//                val scheduleData = state.schedule.data[intent.period]
//                if (scheduleData?.isLoading == true || scheduleData?.isRefreshing == true) return@onIntent
//                dispatch(Message.SetScheduleRefreshing(intent.period))
//                val period = periodsData.selectedPeriod
//                val job = selectPeriodJob
//                selectPeriodJob = launch {
//                    val account = cachedAccount ?: remoteAccountDataRepository.getAccount(authScope)
//                        .also { cachedAccount = it }
//                    job?.cancelAndJoin()
//                    val schedule = remoteScheduleRepository.getSchedule(
//                        auth = authScope,
//                        period = period,
//                        classGroup = account.student.classGroup.first().title
//                    ).toState()
//                    syncDispatch(Message.UpdateSchedule(intent.period, schedule))
//                }
            }
        },
        reducer = {
            when (it) {
                is Message.SetScheduleRefreshing -> {
                    copy(
                        schedule = schedule.copy(
                            data = schedule.data.copy(isRefreshing = true, isLoading = false),
                        )
                    )
                }

                is Message.UpdateSchedule -> copy(
                    schedule = schedule.copy(
                        data = ScheduleListStore.State.ScheduleData(
                            isLoading = false,
                            isRefreshing = false,
                            selectedClass = ScheduleListStore.State.SelectedClass(
                                it.classFullTitle,
                            ),
                            schedule = it.schedule,
                        )
                    ),
                )

                is Message.SetLoading -> copy(
                    schedule = schedule.copy(
                        data = ScheduleListStore.State.ScheduleData(
                            isLoading = true,
                            isRefreshing = false,
                            selectedClass = ScheduleListStore.State.SelectedClass(
                                it.classFullTitle,
                            ),
                            schedule = persistentListOf(),
                        )
                    ),
                )
            }
        },
    ) {

    private sealed interface Message {

        data class UpdateSchedule(
            val classFullTitle: String,
            val schedule: ImmutableList<ScheduleListStore.State.ScheduleDate>,
        ) : Message

        object SetScheduleRefreshing : Message

        data class SetLoading(
            val classFullTitle: String,
        ) : Message

    }

    private sealed interface Action {
        object Setup : Action
    }

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


private fun GetScheduleOutput.toState(): PersistentList<ScheduleListStore.State.ScheduleDate> {
    return schedule.map { item ->
        ScheduleListStore.State.ScheduleDate(
            title = item.title,
            date = item.date,
            lessonGroups = item.lessonGroups.map { lessonGroup ->
                ScheduleListStore.State.LessonGroup(
                    number = lessonGroup.number,
                    lessons = lessonGroup.lessons.map { lesson ->
                        ScheduleListStore.State.Lesson(
                            title = lesson.title,
                            time = lesson.time,
                            teacher = lesson.teacher.formatTeacherName(),
                            group = lesson.group
                        )
                    }.toPersistentList(),
                )
            }.toPersistentList(),
        )
    }.toPersistentList()
}