package ru.sulgik.diary.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate
import org.koin.core.component.get
import ru.sulgik.common.platform.ComparableRange
import ru.sulgik.common.platform.DatePeriod
import ru.sulgik.common.platform.TimeFormatter
import ru.sulgik.core.AuthorizedComponentContext
import ru.sulgik.core.BaseAuthorizedComponentContext
import ru.sulgik.core.childDIContext
import ru.sulgik.core.getStore
import ru.sulgik.core.states
import ru.sulgik.diary.mvi.DiarySettingsStore
import ru.sulgik.diary.mvi.DiaryStore
import ru.sulgik.diary.ui.DiaryScreen
import ru.sulgik.modal.ui.ModalUI
import ru.sulgik.picker.component.PickerComponent
import ru.sulgik.picker.ui.PickerInfo
import java.time.LocalDate

class DiaryComponent(
    componentContext: AuthorizedComponentContext,
) : BaseAuthorizedComponentContext(componentContext) {


    private val store = getStore<DiaryStore>()
    private val settingsStore = getStore<DiarySettingsStore>()
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

    private fun onPickerSelected(pickerInfo: PickerInfo<DatePeriodContainer>) {
        onSelect(
            DatePeriod(
                pickerInfo.data.start.toKotlinLocalDate(),
                pickerInfo.data.end.toKotlinLocalDate()
            )
        )
    }

    val state by store.states(this) {
        onStateUpdated(it)
        it
    }

    val settingsState by settingsStore.states(this)

    private fun onStateUpdated(state: DiaryStore.State) {
        val periodsData = state.periods.data
        if (periodsData?.isOther == true) {
            picker.setData(periodsData.periods.map { period ->
                period.toInfo()
            }, periodsData.selectedPeriod.toInfo())
        } else {
            picker.setData(null)
        }
        val selectedLesson = state.diary.selectedLesson
        if (selectedLesson != null) {
            lessonInfo.showLesson(selectedLesson.date.date, selectedLesson.lesson)
        } else {
            lessonInfo.updateState(false)
        }
    }

    private fun DatePeriod.toInfo(): PickerInfo<DatePeriodContainer> {
        return PickerInfo(
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

    private fun onRefresh(period: DatePeriod) {
        store.accept(DiaryStore.Intent.RefreshDiary(period))
    }

    @Composable
    override fun Content(modifier: Modifier) {
        ModalUI(component = picker) {
            ModalUI(component = lessonInfo) {
                DiaryScreen(
                    periods = state.periods,
                    diary = state.diary,
                    settings = settingsState.settings,
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


