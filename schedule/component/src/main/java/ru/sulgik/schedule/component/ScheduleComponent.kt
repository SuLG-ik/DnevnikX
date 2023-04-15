package ru.sulgik.schedule.component

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
import ru.sulgik.modal.ui.FloatingModalUI
import ru.sulgik.picker.component.PickerComponent
import ru.sulgik.picker.ui.PickerInfo
import ru.sulgik.schedule.mvi.ScheduleStore
import ru.sulgik.schedule.ui.ScheduleScreen
import java.time.LocalDate

class ScheduleComponent(
    componentContext: AuthorizedComponentContext,
    private val backAvailable: Boolean = false,
    private val onBack: () -> Unit = {},
) : BaseAuthorizedComponentContext(componentContext) {

    private val store = getStore<ScheduleStore>()

    private val timeFormatter = get<TimeFormatter>()


    private val currentData = LocalDate.now()

    private val picker =
        PickerComponent(
            componentContext = childDIContext(key = "period_picker"),
            onContinue = this::onPickerSelected,
            marked = { currentData in it.data },
            onHide = { store.accept(ScheduleStore.Intent.HidePeriodSelector) }
        )

    val state by store.states(this) {
        val periodsData = it.periods.data
        if (periodsData?.isOther == true) {
            picker.setData(periodsData.periods.map { period ->
                period.toInfo()
            }, periodsData.selectedPeriod.toInfo())
        } else {
            picker.setData(null)
        }
        it
    }

    private fun onBack() {
        if (backAvailable) {
            onBack.invoke()
        }
    }

    private fun onPickerSelected(info: PickerInfo<DatePeriodContainer>) {
        onSelect(DatePeriod(info.data.start.toKotlinLocalDate(), info.data.end.toKotlinLocalDate()))
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


    private fun onSelect(period: DatePeriod) {
        store.accept(ScheduleStore.Intent.SelectPeriod(period))
    }


    private fun onOther() {
        store.accept(ScheduleStore.Intent.SelectOtherPeriod)
    }

    private fun onRefresh(period: DatePeriod) {
        store.accept(ScheduleStore.Intent.RefreshSchedule(period))
    }

    @Composable
    override fun Content(modifier: Modifier) {
        val state = state
        FloatingModalUI(component = picker) {
            ScheduleScreen(
                state.periods,
                state.schedule,
                backAvailable = backAvailable,
                onSelect = this::onSelect,
                onOther = this::onOther,
                onBack = this::onBack,
                onRefresh = this::onRefresh,
                modifier = modifier,
            )
        }
    }

    @Parcelize
    private data class DatePeriodContainer(
        override val start: LocalDate,
        val end: LocalDate,
    ) : Parcelable, ComparableRange<LocalDate>(start, end)


}