package ru.sulgik.diary.mvi

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
import ru.sulgik.auth.core.AuthScope
import ru.sulgik.common.platform.DatePeriod
import ru.sulgik.core.directReducer
import ru.sulgik.core.syncDispatch
import ru.sulgik.diary.domain.CachedDiaryRepository
import ru.sulgik.diary.domain.data.DiaryOutput
import ru.sulgik.periods.domain.CachedPeriodsRepository
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
                    val diary =
                        cachedDiaryRepository.getDiaryActual(auth, currentPeriod).withState(state)
                    syncDispatch(
                        state.copy(
                            diary = diary,
                        )
                    )
                }
            }

            onIntent<DiaryStore.Intent.SelectOtherPeriod> {
                val state = state
                val periodsData = state.periods.data
                if (state.periods.isLoading || periodsData == null)
                    return@onIntent
                dispatch(
                    state.copy(
                        periods = state.periods.copy(
                            data = periodsData.copy(
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
                            selectedLesson = null,
                        )
                    )
                )
            }
            onIntent<DiaryStore.Intent.HidePeriodSelector> {
                val state = state
                val periodsData = state.periods.data ?: return@onIntent
                dispatch(
                    state.copy(
                        periods = state.periods.copy(
                            data = periodsData.copy(
                                isOther = false,
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
                            selectedLesson = DiaryStore.State.SelectedLesson(
                                date = intent.date,
                                lesson = intent.lesson
                            ),
                        )
                    )
                )
            }
            onIntent<DiaryStore.Intent.SelectPeriodSelector> { intent ->
                val state = state
                val periodsData = state.periods.data
                if (state.periods.isLoading || periodsData == null)
                    return@onIntent
                dispatch(
                    state.copy(
                        periods = state.periods.copy(
                            data = periodsData.copy(
                                selectedPeriod = intent.period,
                                isOther = false,
                            ),
                        ),
                        diary = state.diary.copy(

                        )
                    )
                )
                val job = selectPeriodJob
                selectPeriodJob = launch {
                    job?.cancelAndJoin()
                    var diary = cachedDiaryRepository.getDiaryFast(auth, intent.period)
                        .withState(this@onIntent.state)
                    syncDispatch(
                        this@onIntent.state.copy(
                            diary = diary,
                        )
                    )
                    diary = cachedDiaryRepository.getDiaryActual(auth, intent.period)
                        .withState(this@onIntent.state)
                    syncDispatch(
                        this@onIntent.state.copy(
                            diary = diary,
                        )
                    )
                }
            }
            onIntent<DiaryStore.Intent.RefreshDiary> { intent ->
                val state = state
                val periodsData = state.periods.data
                if (state.periods.isLoading || periodsData == null)
                    return@onIntent
                val selectedPeriod = periodsData.selectedPeriod
                val diaryData = state.diary.data?.get(intent.period)
                if (diaryData?.isLoading == true || diaryData?.isRefreshing == true)
                    return@onIntent
                dispatch(
                    state.copy(
                        diary = state.diary.copy(
                            data = state.diary.data.orFill(intent.period, isRefreshing = true)
                        )
                    )
                )
                val job = selectPeriodJob
                selectPeriodJob = launch {
                    job?.cancelAndJoin()
                    val diary = cachedDiaryRepository.getDiaryActual(auth, selectedPeriod)
                        .withState(this@onIntent.state)
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

private fun Map<DatePeriod, DiaryStore.State.DiaryData>?.orFill(
    period: DatePeriod,
    isRefreshing: Boolean = false,
): MutableMap<DatePeriod, DiaryStore.State.DiaryData> {
    val mutableMap = this?.toMutableMap() ?: mutableMapOf()
    val data = mutableMap[period]?.copy(isRefreshing = false, isLoading = true)
        ?: DiaryStore.State.DiaryData(
            isLoading = !isRefreshing,
            isRefreshing = isRefreshing,
            diary = emptyList(),
        )
    mutableMap[period] = data
    return mutableMap

}

private fun DiaryOutput.withState(state: DiaryStore.State): DiaryStore.State.Diary {
    return state.diary.copy(
        data = state.diary.data.let {
            val mutableMap = it?.toMutableMap() ?: mutableMapOf()
            mutableMap[period] = this.toState()
            mutableMap
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
}


fun getPeriod(date: kotlinx.datetime.LocalDate): DatePeriod {
    val start = date.minus((date.dayOfWeek.value - 1).toLong(), DateTimeUnit.DAY)
    return DatePeriod(start, start.plus(6, DateTimeUnit.DAY))
}