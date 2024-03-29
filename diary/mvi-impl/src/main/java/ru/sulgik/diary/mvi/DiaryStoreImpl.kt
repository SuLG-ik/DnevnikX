package ru.sulgik.diary.mvi

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.utils.ExperimentalMviKotlinApi
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineExecutorFactory
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.mutate
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toKotlinLocalDate
import org.koin.core.annotation.Factory
import ru.sulgik.auth.core.AuthScope
import ru.sulgik.common.platform.DatePeriod
import ru.sulgik.diary.domain.CachedDiaryRepository
import ru.sulgik.diary.domain.data.DiaryOutput
import ru.sulgik.kacher.core.on
import ru.sulgik.periods.domain.CachedPeriodsRepository
import ru.sulgik.periods.domain.data.GetPeriodsOutput
import java.time.LocalDate


@OptIn(ExperimentalMviKotlinApi::class)
@Factory(binds = [DiaryStore::class])
class DiaryStoreImpl(
    storeFactory: StoreFactory,
    coroutineDispatcher: CoroutineDispatcher,
    auth: AuthScope,
    cachedPeriodsRepository: CachedPeriodsRepository,
    cachedDiaryRepository: CachedDiaryRepository,
) : DiaryStore,
    Store<DiaryStore.Intent, DiaryStore.State, DiaryStore.Label> by storeFactory.create<_, Action, Message, _, _>(
        name = "DiaryStoreImpl",
        initialState = DiaryStore.State(),
        bootstrapper = coroutineBootstrapper {
            dispatch(Action.Setup)
        },
        executorFactory = coroutineExecutorFactory(coroutineDispatcher) {
            var selectPeriodJob: Job? = null
            onAction<Action.Setup> {
                val periodsRequest = cachedPeriodsRepository.getPeriods(auth)
                launch(Dispatchers.Main) {
                    periodsRequest.on(
                        statusUpdated = { periodsStatus ->
                            periodsStatus.data?.periods?.toState()?.let { periods ->
                                dispatch(Message.UpdatePeriods(periods))
                                cachedDiaryRepository.getDiary(
                                    auth = auth,
                                    period = periods.selectedPeriod
                                ).on(
                                    statusUpdated = { diaryStatus ->
                                        diaryStatus.data?.let { diary ->
                                            dispatch(
                                                Message.UpdateDiary(diary.period, diary.toState())
                                            )
                                        }
                                    }
                                )
                            }
                        },
                    )
                }
            }

            onIntent<DiaryStore.Intent.SelectOtherPeriod> {
                val periodsData = state.periods.data
                if (state.periods.isLoading || periodsData == null)
                    return@onIntent
                dispatch(Message.ShowOtherPeriods(true))
            }
            onIntent<DiaryStore.Intent.HideLessonInfo> {
                dispatch(Message.SelectLesson(null))
            }
            onIntent<DiaryStore.Intent.HidePeriodSelector> {
                state.periods.data ?: return@onIntent
                dispatch(Message.ShowOtherPeriods(false))
            }
            onIntent<DiaryStore.Intent.ShowLessonInfo> { intent ->
                dispatch(
                    Message.SelectLesson(
                        DiaryStore.State.SelectedLesson(
                            date = intent.date,
                            lesson = intent.lesson
                        ),
                    )
                )
            }
            onIntent<DiaryStore.Intent.SelectPeriodSelector> { intent ->
                val periodsData = state.periods.data
                if (state.periods.isLoading || periodsData == null || periodsData.selectedPeriod == intent.period)
                    return@onIntent
                dispatch(Message.SelectPeriod(intent.period))
                val job = selectPeriodJob
                val diaryRequest = cachedDiaryRepository.getDiary(auth, intent.period)
                selectPeriodJob = launch(Dispatchers.Main) {
                    job?.cancelAndJoin()
                    diaryRequest.on(
                        statusUpdated = { diaryStatus ->
                            diaryStatus.data?.let {
                                dispatch(
                                    Message.UpdateDiary(
                                        intent.period,
                                        it.toState(),
                                    )
                                )
                            }
                        }
                    )
                }
            }
            onIntent<DiaryStore.Intent.RefreshDiary> { intent ->
                val periodsData = state.periods.data
                if (state.periods.isLoading || periodsData == null)
                    return@onIntent
                val diaryData = state.diary.data[intent.period]
                if (diaryData?.isLoading == true || diaryData?.isRefreshing == true)
                    return@onIntent
                dispatch(Message.SetDiaryRefreshing(intent.period))
                val job = selectPeriodJob
                val diaryRequest = cachedDiaryRepository.getDiaryActual(auth, intent.period)
                selectPeriodJob = launch(Dispatchers.Main) {
                    job?.cancelAndJoin()
                    diaryRequest.on(
                        statusUpdated = { diaryStatus ->
                            diaryStatus.data?.let {
                                dispatch(
                                    Message.UpdateDiary(
                                        intent.period,
                                        it.toState(),
                                    )
                                )
                            }
                        }
                    )
                }
            }
        },
        reducer = {
            when (it) {
                is Message.SelectLesson -> copy(
                    diary = diary.copy(
                        selectedLesson = it.lesson?.let { lesson ->
                            DiaryStore.State.SelectedLesson(
                                date = lesson.date,
                                lesson = lesson.lesson
                            )
                        },
                    )
                )

                is Message.SelectPeriod -> copy(
                    periods = periods.copy(
                        data = periods.data?.copy(
                            selectedPeriod = it.period,
                            isOther = false,
                        ),
                    ),
                )

                is Message.SetDiaryRefreshing ->
                    copy(
                        diary = diary.copy(
                            data = diary.data.orFill(it.period, isRefreshing = true)
                        )
                    )

                is Message.ShowOtherPeriods ->
                    copy(
                        periods = periods.copy(
                            data = periods.data?.copy(
                                isOther = it.value,
                            )
                        )
                    )

                is Message.UpdateDiary ->
                    copy(
                        diary = it.diary.withState(this, it.period),
                    )

                is Message.UpdatePeriods ->
                    copy(
                        periods = DiaryStore.State.Periods(
                            isLoading = false,
                            data = it.periods,
                        )
                    )
            }
        }
    ) {


    private sealed interface Message {

        data class UpdatePeriods(val periods: DiaryStore.State.PeriodsData) : Message

        data class ShowOtherPeriods(val value: Boolean) : Message

        data class SelectPeriod(val period: DatePeriod) : Message

        data class UpdateDiary(val period: DatePeriod, val diary: DiaryStore.State.DiaryData) :
            Message

        data class SetDiaryRefreshing(val period: DatePeriod) : Message

        data class SelectLesson(val lesson: DiaryStore.State.SelectedLesson?) : Message

    }

    private sealed interface Action {
        object Setup : Action
    }

}

private fun List<GetPeriodsOutput.Period>.toState(): DiaryStore.State.PeriodsData {
    val periods = flatMap { it.nestedPeriods }
    val currentDate = LocalDate.now().toKotlinLocalDate()
    val currentPeriod = getPeriod(currentDate)
    val nextPeriod = getPeriod(currentDate.plus(1, DateTimeUnit.WEEK))
    val previousPeriod = getPeriod(currentDate.minus(1, DateTimeUnit.WEEK))
    return DiaryStore.State.PeriodsData(
        currentPeriod = if (currentPeriod in periods) currentPeriod else null,
        nextPeriod = if (nextPeriod in periods) nextPeriod else null,
        previousPeriod = if (previousPeriod in periods) previousPeriod else null,
        selectedPeriod = currentPeriod,
        periods = periods.toPersistentList(),
        isOther = false,
    )
}

private fun ImmutableMap<DatePeriod, DiaryStore.State.DiaryData>?.orFill(
    period: DatePeriod,
    isRefreshing: Boolean = false,
): ImmutableMap<DatePeriod, DiaryStore.State.DiaryData> {
    return this?.toPersistentMap()?.mutate {
        val data = it[period]?.copy(isRefreshing = isRefreshing, isLoading = !isRefreshing)
            ?: DiaryStore.State.DiaryData(
                isLoading = true,
                isRefreshing = false,
                diary = persistentListOf(),
            )
        it[period] = data
    } ?: persistentMapOf()
}

private fun DiaryStore.State.DiaryData.withState(
    state: DiaryStore.State,
    period: DatePeriod
): DiaryStore.State.Diary {
    return state.diary.copy(
        data = state.diary.data.let { diary ->
            diary.toPersistentMap().mutate {
                it[period] = this
            }
        }
    )
}

private fun DiaryOutput.toState(): DiaryStore.State.DiaryData {
    return DiaryStore.State.DiaryData(
        isLoading = false,
        isRefreshing = false,
        diary = diary.map { diaryItem ->
            DiaryStore.State.DiaryDate(
                date = diaryItem.date,
                alert = diaryItem.alert?.let {
                    DiaryStore.State.DiaryAlert(
                        isOverload = it.alert == "holiday" || it.alert == "vacation",
                        message = it.message,
                    )
                },
                lessons = diaryItem.lessons.map { lesson ->
                    DiaryStore.State.Lesson(
                        number = lesson.number,
                        title = lesson.title,
                        time = lesson.time,
                        homework = lesson.homework.map { homework ->
                            DiaryStore.State.Homework(homework.text)
                        }.toPersistentList(),
                        files = lesson.files.map { file ->
                            DiaryStore.State.File(file.name, file.url)
                        }.toPersistentList(),
                        marks = lesson.marks.map { mark ->
                            DiaryStore.State.Mark(mark.mark, mark.value)
                        }.toPersistentList(),
                    )
                }.toPersistentList()
            )
        }.toPersistentList()
    )
}


fun getPeriod(date: kotlinx.datetime.LocalDate): DatePeriod {
    val start = date.minus((date.dayOfWeek.value - 1).toLong(), DateTimeUnit.DAY)
    return DatePeriod(start, start.plus(6, DateTimeUnit.DAY))
}