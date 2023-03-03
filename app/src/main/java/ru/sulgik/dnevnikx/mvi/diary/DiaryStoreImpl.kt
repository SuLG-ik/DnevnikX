package ru.sulgik.dnevnikx.mvi.diary

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.utils.ExperimentalMviKotlinApi
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineExecutorFactory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toKotlinLocalDate
import org.koin.core.annotation.Factory
import ru.sulgik.dnevnikx.data.AuthScope
import ru.sulgik.dnevnikx.mvi.directReducer
import ru.sulgik.dnevnikx.mvi.syncDispatch
import ru.sulgik.dnevnikx.platform.DatePeriod
import ru.sulgik.dnevnikx.repository.data.DiaryOutput
import ru.sulgik.dnevnikx.repository.diary.CachedDiaryRepository
import ru.sulgik.dnevnikx.repository.periods.CachedPeriodsRepository
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
    Store<DiaryStore.Intent, DiaryStore.State, DiaryStore.Label> by storeFactory.create<_, Action, _, _, _>(
        name = "DiaryStoreImpl",
        initialState = DiaryStore.State(),
        bootstrapper = coroutineBootstrapper {
            dispatch(Action.Setup)
        },
        executorFactory = coroutineExecutorFactory(coroutineDispatcher) {
            val cache = mutableMapOf<DatePeriod, DiaryStore.State.Diary>()
            var selectPeriodJob: Job? = null
            onAction<Action.Setup> {
                selectPeriodJob = launch {
                    val periods =
                        cachedPeriodsRepository.getPeriodsFast(auth).periods.flatMap { it.nestedPeriods }
                    val currentDate = LocalDate.now().toKotlinLocalDate()
                    val currentPeriod = getPeriod(currentDate)
                    val nextPeriod = getPeriod(currentDate.plus(1, DateTimeUnit.WEEK))
                    val previousPeriod = getPeriod(currentDate.minus(1, DateTimeUnit.WEEK))
                    syncDispatch(
                        DiaryStore.State(
                            periods = DiaryStore.State.Periods(
                                isLoading = false,
                                data = DiaryStore.State.PeriodsData(
                                    currentPeriod = if (currentPeriod in periods) currentPeriod else null,
                                    nextPeriod = if (nextPeriod in periods) nextPeriod else null,
                                    previousPeriod = if (previousPeriod in periods) previousPeriod else null,
                                    selectedPeriod = currentPeriod,
                                    periods = periods,
                                    isOther = false,
                                ),
                            )
                        )
                    )
                    var diary =
                        cachedDiaryRepository.getDiaryFast(auth, currentPeriod).toState(state)
                    cache[currentPeriod] = diary
                    syncDispatch(
                        state.copy(
                            diary = diary,
                        )
                    )
                    diary = cachedDiaryRepository.getDiaryActual(auth, currentPeriod).toState(state)
                    cache[currentPeriod] = diary
                    syncDispatch(
                        state.copy(
                            diary = diary,
                        )
                    )
                }
            }

            onIntent<DiaryStore.Intent.SelectOtherPeriod> {
                val state = state
                if (state.periods.isLoading || state.periods.data == null)
                    return@onIntent
                dispatch(
                    state.copy(
                        periods = state.periods.copy(
                            data = state.periods.data.copy(
                                isOther = true
                            )
                        )
                    )
                )
            }
            onIntent<DiaryStore.Intent.HideLessonInfo> {
                val state = state
                if (state.diary.data == null) {
                    return@onIntent
                }
                dispatch(
                    state.copy(
                        diary = state.diary.copy(
                            data = state.diary.data.copy(
                                selectedLesson = null,
                            )
                        )
                    )
                )
            }
            onIntent<DiaryStore.Intent.ShowLessonInfo> { intent ->
                val state = state
                if (state.diary.data == null) {
                    return@onIntent
                }
                dispatch(
                    state.copy(
                        diary = state.diary.copy(
                            data = state.diary.data.copy(
                                selectedLesson = DiaryStore.State.SelectedLesson(
                                    date = intent.date,
                                    lesson = intent.lesson
                                ),
                            )
                        )
                    )
                )
            }
            onIntent<DiaryStore.Intent.SelectPeriod> { intent ->
                val state = state
                if (state.periods.isLoading || state.periods.data == null)
                    return@onIntent
                dispatch(
                    state.copy(
                        diary = DiaryStore.State.Diary(),
                        periods = state.periods.copy(
                            data = state.periods.data.copy(
                                selectedPeriod = intent.period,
                                isOther = false,
                            ),
                        )
                    )
                )
                val cachedDiary = cache[intent.period]
                if (cachedDiary != null) {
                    dispatch(
                        this@onIntent.state.copy(
                            diary = cachedDiary,
                        )
                    )
                }
                val job = selectPeriodJob
                selectPeriodJob = launch {
                    job?.cancelAndJoin()
                    var diary = cachedDiaryRepository.getDiaryFast(auth, intent.period)
                        .toState(this@onIntent.state)
                    cache[intent.period] = diary
                    syncDispatch(
                        this@onIntent.state.copy(
                            diary = diary,
                        )
                    )
                    diary = cachedDiaryRepository.getDiaryActual(auth, intent.period)
                        .toState(this@onIntent.state)
                    cache[intent.period] = diary
                    syncDispatch(
                        this@onIntent.state.copy(
                            diary = diary,
                        )
                    )
                }
            }
            onIntent<DiaryStore.Intent.RefreshDiary> { intent ->
                val state = state
                if (state.periods.isLoading || state.periods.data == null || state.diary.isRefreshing || state.diary.isLoading)
                    return@onIntent
                val selectedPeriod = state.periods.data.selectedPeriod
                val job = selectPeriodJob
                dispatch(
                    state.copy(
                        diary = state.diary.copy(
                            isRefreshing = true
                        )
                    )
                )
                selectPeriodJob = launch {
                    job?.cancelAndJoin()
                    val diary = cachedDiaryRepository.getDiaryActual(auth, selectedPeriod)
                        .toState(this@onIntent.state)
                    cache[selectedPeriod] = diary
                    syncDispatch(
                        this@onIntent.state.copy(
                            diary = diary,
                            isRefreshing = false,
                        )
                    )
                }
            }
        },
        reducer = directReducer(),
    ) {


    private sealed interface Action {
        object Setup : Action
    }

}

private fun DiaryOutput.toState(state: DiaryStore.State): DiaryStore.State.Diary {
    return DiaryStore.State.Diary(
        isLoading = false,
        data = DiaryStore.State.DiaryData(
            selectedLesson = state.diary.data?.selectedLesson,
            diary = diary.map { diaryItem ->
                DiaryStore.State.DiaryDate(
                    date = diaryItem.date,
                    alert = diaryItem.alert?.let {
                        DiaryStore.State.DiaryAlert(
                            isOverload = it.alert == "holiday",
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
                            },
                            files = lesson.files.map { file ->
                                DiaryStore.State.File(file.name, file.url)
                            },
                            marks = lesson.marks.map { mark ->
                                DiaryStore.State.Mark(mark.mark, mark.value)
                            },
                        )
                    }
                )
            }
        )
    )
}

fun getPeriod(date: kotlinx.datetime.LocalDate): DatePeriod {
    val start = date.minus((date.dayOfWeek.value - 1).toLong(), DateTimeUnit.DAY)
    return DatePeriod(start, start.plus(6, DateTimeUnit.DAY))
}