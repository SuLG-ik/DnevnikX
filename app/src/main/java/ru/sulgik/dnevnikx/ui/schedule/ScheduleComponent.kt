package ru.sulgik.dnevnikx.ui.schedule

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate
import org.koin.core.component.get
import ru.sulgik.dnevnikx.mvi.getStore
import ru.sulgik.dnevnikx.mvi.schedule.ScheduleStore
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

class ScheduleComponent(
    componentContext: AuthorizedComponentContext,
    private val backAvailable: Boolean = false,
    private val onBack: () -> Unit = {},
) : BaseAuthorizedComponentContext(componentContext) {

    val store = getStore<ScheduleStore>()

    private val timeFormatter = get<TimeFormatter>()


    private val currentData = LocalDate.now()

    private val picker =
        PickerComponent(
            componentContext = childDIContext(key = "period_picker"),
            onContinue = this::onPickerSelected,
            marked = { currentData in it.data }
        )

    val state by store.states(this) {
        if (it.periods.data?.isOther == true) {
            picker.setData(it.periods.data.periods.map { period ->
                period.toInfo()
            }, it.periods.data.selectedPeriod.toInfo())
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

    private fun onPickerSelected(info: PickerComponent.Info<DatePeriodContainer>) {
        onSelect(DatePeriod(info.data.start.toKotlinLocalDate(), info.data.end.toKotlinLocalDate()))
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


    private fun onSelect(period: DatePeriod) {
        store.accept(ScheduleStore.Intent.SelectPeriod(period))
    }


    private fun onOther() {
        store.accept(ScheduleStore.Intent.SelectOtherPeriod)
    }

    @Composable
    override fun Content(modifier: Modifier) {
        val state = state
        ModalUI(component = picker) {
            ScheduleScreen(
                state.periods,
                state.schedule,
                backAvailable = backAvailable,
                onSelect = this::onSelect,
                onOther = this::onOther,
                onBack = this::onBack,
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