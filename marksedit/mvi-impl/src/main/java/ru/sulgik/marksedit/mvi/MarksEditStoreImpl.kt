package ru.sulgik.marksedit.mvi

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.utils.ExperimentalMviKotlinApi
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineExecutorFactory
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.plus
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.sulgik.auth.core.AuthScope
import ru.sulgik.kacher.core.on
import ru.sulgik.marks.domain.CachedMarksRepository
import ru.sulgik.marks.domain.data.LessonOutput
import ru.sulgik.marks.domain.data.MarksOutput
import java.math.BigDecimal
import java.math.RoundingMode


@OptIn(ExperimentalMviKotlinApi::class)
class MarksEditStoreImpl(
    storeFactory: StoreFactory,
    coroutineDispatcher: CoroutineDispatcher,
    marksRepository: CachedMarksRepository,
    auth: AuthScope,
    params: MarksEditStore.Params,
) : MarksEditStore,
    Store<MarksEditStore.Intent, MarksEditStore.State, MarksEditStore.Label> by storeFactory.create<_, Action, Message, _, _>(
        name = "MarksEditStoreImpl",
        initialState = MarksEditStore.State(),
        bootstrapper = coroutineBootstrapper(coroutineDispatcher) {
            dispatch(Action.Setup)
        },
        executorFactory = coroutineExecutorFactory(coroutineDispatcher) {
            onAction<Action.Setup> {
                launch(Dispatchers.Main) {
                    marksRepository.getLesson(
                        auth = auth,
                        period = MarksOutput.Period(
                            params.period.title,
                            params.period.period,
                        ),
                        title = params.title
                    ).on {
                        val data = it.data
                        if (data != null) {
                            dispatch(
                                Message.SetupData(
                                    period = data.toPeriodState(),
                                    lesson = data.toLessonState(),
                                )
                            )
                        }
                    }
                }
            }
            onIntent<MarksEditStore.Intent.AddMark> {
                val data = state.lessonData.data ?: return@onIntent
                dispatch(
                    Message.ChangeMarks(
                        data.marks + MarksEditStore.State.Mark(
                            status = MarksEditStore.State.MarkStatus.ENABLED,
                            date = null,
                            value = it.value,
                            mark = it.value.toString(),
                        )
                    )
                )
            }
            onIntent<MarksEditStore.Intent.ChangeStatus> {
                val data = state.lessonData.data ?: return@onIntent
                val targetMark = data.marks.getOrNull(it.index) ?: return@onIntent
                if (targetMark.date == null) {
                    dispatch(
                        Message.ChangeMarks(
                            marks = data.marks.removeAt(it.index)
                        )
                    )
                    return@onIntent
                }
                dispatch(
                    Message.ChangeMarks(
                        data.marks.set(
                            index = it.index,
                            element = targetMark.copy(status = targetMark.status.inverse())
                        )
                    )
                )
            }
            onIntent<MarksEditStore.Intent.Clear> {
                val data = state.lessonData.data ?: return@onIntent
                dispatch(
                    Message.ChangeMarks(
                        data.marks.mapNotNull { if (it.date != null) it.copy(status = MarksEditStore.State.MarkStatus.ENABLED) else null }
                            .toPersistentList()
                    )
                )
            }
        },
        reducer = {
            when (it) {
                is Message.SetupData -> copy(
                    lessonData = lessonData.copy(
                        isLoading = false,
                        period = it.period,
                        data = it.lesson
                    )
                )

                is Message.ChangeMarks -> {
                    val data = lessonData.data
                    val average = it.marks.calculateAverage()
                    copy(
                        changes = average.third,
                        lessonData = lessonData.copy(
                            isLoading = false,
                            data = data?.copy(
                                average = average.second,
                                averageValue = average.first,
                                marks = it.marks
                            )
                        )
                    )
                }
            }
        },
    ) {


    private sealed interface Message {
        data class SetupData(
            val period: MarksEditStore.State.Period,
            val lesson: MarksEditStore.State.Lesson,
        ) : Message

        data class ChangeMarks(
            val marks: PersistentList<MarksEditStore.State.Mark>,
        ) : Message

    }

    private sealed interface Action {
        object Setup : Action
    }

}

private fun List<MarksEditStore.State.Mark>.calculateAverage(): Triple<Int, String, MarksEditStore.State.Changes> {
    var count = 0
    var sum = 0
    val changesCount = mutableMapOf<Int, Int>()
    forEach {
        if (it.value == 0) return@forEach
        changesCount.compute(it.value) { _, prev ->
            val delta = when {
                it.status == MarksEditStore.State.MarkStatus.DISABLED -> -1
                it.date == null -> 1
                else -> 0
            }
            prev?.plus(delta) ?: delta
        }
        if (it.status == MarksEditStore.State.MarkStatus.ENABLED) {
            count += 1
            sum += it.value
        }
    }
    val changes =
        MarksEditStore.State.Changes(
            changes = changesCount.toPersistentMap(),
        )
    if (count == 0 || sum == 0) {
        return Triple(0, "0", changes)
    }
    val average = sum.toDouble() / count.toDouble()
    val averageValue = when {
        average >= 4.5f -> 5
        average >= 3.5f -> 4
        average >= 2.5f -> 3
        else -> 2
    }
    val bd = BigDecimal(average)
    val roundoff = bd.setScale(2, RoundingMode.HALF_UP)
    return Triple(averageValue, roundoff.toEngineeringString().removeSuffix(".00"), changes)
}

private fun MarksEditStore.State.MarkStatus.inverse(): MarksEditStore.State.MarkStatus {
    return when (this) {
        MarksEditStore.State.MarkStatus.DISABLED -> MarksEditStore.State.MarkStatus.ENABLED
        MarksEditStore.State.MarkStatus.ENABLED -> MarksEditStore.State.MarkStatus.DISABLED
    }
}

private fun LessonOutput.toLessonState(): MarksEditStore.State.Lesson {
    return MarksEditStore.State.Lesson(
        title = lesson.title,
        average = lesson.average,
        averageValue = lesson.averageValue,
        marks = lesson.marks.map { mark ->
            MarksEditStore.State.Mark(
                status = MarksEditStore.State.MarkStatus.ENABLED,
                mark = mark.mark,
                value = mark.value,
                date = mark.date,
            )
        }.toPersistentList()
    )
}

private fun LessonOutput.toPeriodState(): MarksEditStore.State.Period {
    return MarksEditStore.State.Period(
        title = period.title,
    )
}
