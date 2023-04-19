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
import ru.sulgik.kacher.core.on
import ru.sulgik.schedule.domain.CachedScheduleRepository
import ru.sulgik.schedule.domain.data.GetScheduleOutput

@OptIn(ExperimentalMviKotlinApi::class)
class ScheduleListStoreImpl(
    storeFactory: StoreFactory,
    coroutineDispatcher: CoroutineDispatcher,
    authScope: AuthScope,
    cachedScheduleRepository: CachedScheduleRepository,
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
                    cachedScheduleRepository.getSchedule(
                        auth = authScope,
                        classGroup = it.classFullTitle,
                    ).on { response ->
                        val data = response.data?.toState()
                        if (data != null) {
                            syncDispatch(
                                Message.UpdateSchedule(
                                    classFullTitle = it.classFullTitle,
                                    schedule = data
                                )
                            )
                        }
                    }
                }
            }
            onIntent<ScheduleListStore.Intent.RefreshSchedule> {
                val selectedClass = state.schedule.data.selectedClass ?: return@onIntent
                dispatch(Message.SetRefreshing)
                val job = classLoadingJob
                classLoadingJob = launch(Dispatchers.Main) {
                    job?.cancel()
                    cachedScheduleRepository.getScheduleActual(
                        auth = authScope,
                        classGroup = selectedClass.fullTitle,
                    ).on { response ->
                        val data = response.data?.toState()
                        if (data != null) {
                            syncDispatch(
                                Message.UpdateSchedule(
                                    classFullTitle = selectedClass.fullTitle,
                                    schedule = data
                                )
                            )
                        }
                    }
                }
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

                Message.SetRefreshing -> copy(
                    schedule = schedule.copy(
                        data = schedule.data.copy(
                            isLoading = false,
                            isRefreshing = true,
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

        object SetRefreshing : Message

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