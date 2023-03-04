package ru.sulgik.dnevnikx.ui.diary

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate
import org.koin.core.component.get
import ru.sulgik.dnevnikx.mvi.diary.DiaryStore
import ru.sulgik.dnevnikx.mvi.getStore
import ru.sulgik.dnevnikx.mvi.states
import ru.sulgik.dnevnikx.platform.ComparableRange
import ru.sulgik.dnevnikx.platform.DatePeriod
import ru.sulgik.dnevnikx.platform.TimeFormatter
import ru.sulgik.dnevnikx.ui.AuthorizedComponentContext
import ru.sulgik.dnevnikx.ui.BaseAuthorizedComponentContext
import ru.sulgik.dnevnikx.ui.ModalUI
import ru.sulgik.dnevnikx.ui.childDIContext
import ru.sulgik.dnevnikx.ui.picker.PickerComponent
import java.time.LocalDate

class DiaryComponent(
    componentContext: AuthorizedComponentContext,
) : BaseAuthorizedComponentContext(componentContext) {


    private val store = getStore<DiaryStore>()
    private val timeFormatter = get<TimeFormatter>()

    private val currentData = LocalDate.now()

    private val picker =
        PickerComponent(
            componentContext = childDIContext(key = "period_picker"),
            onContinue = this::onPickerSelected,
            marked = { currentData in it.data },
            onHide = { store.accept(DiaryStore.Intent.HidePeriodSelector) }
        )

    private val lessonInfo = DiaryLessonInfoComponent(
        componentContext = childDIContext(key = "lesson_info"),
        onHide = {
            store.accept(DiaryStore.Intent.HideLessonInfo)
        }
    )

    private fun onPickerSelected(info: PickerComponent.Info<DatePeriodContainer>) {
        onSelect(DatePeriod(info.data.start.toKotlinLocalDate(), info.data.end.toKotlinLocalDate()))
    }

    val state by store.states(this) {
        onStateUpdated(it)
        it
    }

    private fun onStateUpdated(state: DiaryStore.State) {
        if (state.periods.data?.isOther == true) {
            picker.setData(state.periods.data.periods.map { period ->
                period.toInfo()
            }, state.periods.data.selectedPeriod.toInfo())
        } else {
            picker.setData(null)
        }
        val selectedLesson = state.diary.data?.selectedLesson
        if (selectedLesson != null) {
            lessonInfo.showLesson(selectedLesson.date.date, selectedLesson.lesson)
        } else {
            lessonInfo.updateState(false)
        }
    }

    private fun DatePeriod.toInfo(): PickerComponent.Info<DatePeriodContainer> {
        return PickerComponent.Info(
            DatePeriodContainer(
                start.toJavaLocalDate(),
                end.toJavaLocalDate()
            ),
            timeFormatter.format(this)
        )
    }

    private fun onLesson(date: DiaryStore.State.DiaryDate, lesson: DiaryStore.State.Lesson) {
        store.accept(DiaryStore.Intent.ShowLessonInfo(date = date, lesson = lesson))
    }

    private fun onRefresh() {
        store.accept(DiaryStore.Intent.RefreshDiary)
    }

    @Composable
    override fun Content(modifier: Modifier) {
        ModalUI(component = picker) {
            ModalUI(component = lessonInfo) {
                DiaryScreen(
                    periods = state.periods,
                    diary = state.diary,
                    onSelect = this::onSelect,
                    onOther = this::onOther,
                    onLesson = this::onLesson,
                    onRefresh = this::onRefresh,
                    modifier = modifier
                )
            }
        }
    }

    private fun onSelect(period: DatePeriod) {
        store.accept(DiaryStore.Intent.SelectPeriodSelector(period))
    }


    private fun onOther() {
        store.accept(DiaryStore.Intent.SelectOtherPeriod)
    }

    @Parcelize
    private data class DatePeriodContainer(
        override val start: LocalDate,
        val end: LocalDate,
    ) : Parcelable, ComparableRange<LocalDate>(start, end)


}


