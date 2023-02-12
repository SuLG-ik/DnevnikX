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
import ru.sulgik.dnevnikx.repository.diary.RemoteDiaryRepository
import ru.sulgik.dnevnikx.repository.periods.RemotePeriodsRepository
import java.time.LocalDate


@OptIn(ExperimentalMviKotlinApi::class)
@Factory(binds = [DiaryStore::class])
class DiaryStoreImpl(
    storeFactory: StoreFactory,
    coroutineDispatcher: CoroutineDispatcher,
    auth: AuthScope,
    remotePeriodsRepository: RemotePeriodsRepository,
    remoteDiaryRepository: RemoteDiaryRepository,
) : DiaryStore,
    Store<DiaryStore.Intent, DiaryStore.State, DiaryStore.Label> by storeFactory.create<_, Action, _, _, _>(
        name = "DiaryStoreImpl",
        initialState = DiaryStore.State(),
        bootstrapper = coroutineBootstrapper {
            dispatch(Action.Setup)
        },
        executorFactory = coroutineExecutorFactory(coroutineDispatcher) {
            val cache = mutableMapOf<DatePeriod, DiaryStore.State.Diary>()
            onAction<Action.Setup> {
                launch {
                    val periods =
                        remotePeriodsRepository.getPeriods(auth).periods.flatMap { it.nestedPeriods }
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
                    val diary = remoteDiaryRepository.getDiary(auth, currentPeriod).toState()
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
            var selectPeriodJob: Job? = null
            onIntent<DiaryStore.Intent.SelectPeriod> {
                val state = state
                if (state.periods.isLoading || state.periods.data == null)
                    return@onIntent
                dispatch(
                    state.copy(
                        diary = DiaryStore.State.Diary(),
                        periods = state.periods.copy(
                            data = state.periods.data.copy(
                                selectedPeriod = it.period,
                                isOther = false,
                            ),
                            )
                    )
                )
                val cachedDiary = cache[it.period]
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
                    val diary = remoteDiaryRepository.getDiary(auth, it.period).toState()
                    cache[it.period] = diary
                    syncDispatch(
                        this@onIntent.state.copy(
                            diary = diary,
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

private fun DiaryOutput.toState(): DiaryStore.State.Diary {
    return DiaryStore.State.Diary(
        isLoading = false,
        data = DiaryStore.State.DiaryData(
            diary = diary.map { diaryItem ->
                DiaryStore.State.DiaryDate(
                    diaryItem.date,
                    diaryItem.lessons.map { lesson ->
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
                            }
                        )
                    }
                )
            }
        )
    )
}

private fun getPeriod(date: kotlinx.datetime.LocalDate): DatePeriod {
    val start = date.minus((date.dayOfWeek.value - 1).toLong(), DateTimeUnit.DAY)
    return DatePeriod(start, start.plus(6, DateTimeUnit.DAY))
}