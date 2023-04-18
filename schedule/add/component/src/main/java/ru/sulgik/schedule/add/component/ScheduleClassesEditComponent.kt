package ru.sulgik.schedule.add.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import ru.sulgik.core.AuthorizedComponentContext
import ru.sulgik.core.BaseAuthorizedComponentContext
import ru.sulgik.core.childDIContext
import ru.sulgik.core.getStore
import ru.sulgik.core.states
import ru.sulgik.picker.component.PickerComponent
import ru.sulgik.picker.ui.PickerInfo
import ru.sulgik.schedule.add.mvi.ScheduleClassesEditStore
import ru.sulgik.schedule.add.ui.ScheduleEditScreen

class ScheduleClassesEditComponent(
    componentContext: AuthorizedComponentContext,
    private val backAvailable: Boolean = false,
    private val onBack: () -> Unit = {},
) : BaseAuthorizedComponentContext(componentContext) {


    private val store = getStore<ScheduleClassesEditStore>()


    private val numberPicker =
        PickerComponent(
            componentContext = childDIContext(key = "number_picker"),
            onContinue = this::onNumberChanged,
            setupData = generateNumbers()
        )

    private val groupPicker =
        PickerComponent(
            componentContext = childDIContext(key = "group_picker"),
            onContinue = this::onGroupChanged,
            setupData = generateGroups(),
        )

    private val state by store.states(this)

    private fun onBack() {
        if (backAvailable) {
            onBack.invoke()
        }
    }

    private fun onNumberChanged(info: PickerInfo<StringContainer>) {
        store.accept(ScheduleClassesEditStore.Intent.SelectNumber(info.data.value))
    }

    private fun onGroupChanged(info: PickerInfo<StringContainer>) {
        store.accept(ScheduleClassesEditStore.Intent.SelectGroup(info.data.value))
    }

    private fun onAddClass() {
        store.accept(ScheduleClassesEditStore.Intent.AddClass)
    }

    private fun onDeleteClass(number: String, group: String) {
        store.accept(ScheduleClassesEditStore.Intent.DeleteClass(number, group))
    }

    @Composable
    override fun Content(modifier: Modifier) {
        ScheduleEditScreen(
            classes = state.savedClasses,
            selector = state.classSelector,
            onAddClass = this::onAddClass,
            onDeleteClass = this::onDeleteClass,
            backAvailable = backAvailable,
            onBack = onBack,
            modifier = modifier,
            classGroupSelectorContent = {
                groupPicker.Content(Modifier)
            },
            classNumberSelectorContent = {
                numberPicker.Content(Modifier)
            }
        )
    }

}

@JvmInline
@Parcelize
private value class StringContainer(
    val value: String,
) : Parcelable


private fun generateNumbers(): PickerComponent.Data<StringContainer> {
    val list = listOf(
        PickerInfo(StringContainer("1"), "1 класс"),
        PickerInfo(StringContainer("2"), "2 класс"),
        PickerInfo(StringContainer("3"), "3 класс"),
        PickerInfo(StringContainer("4"), "4 класс"),
        PickerInfo(StringContainer("5"), "5 класс"),
        PickerInfo(StringContainer("6"), "6 класс"),
        PickerInfo(StringContainer("7"), "7 класс"),
        PickerInfo(StringContainer("8"), "8 класс"),
        PickerInfo(StringContainer("9"), "9 класс"),
        PickerInfo(StringContainer("10"), "10 класс"),
        PickerInfo(StringContainer("11"), "11 класс"),
    )
    return PickerComponent.Data(
        setupData = list.first(),
        list = list,
    )
}


private fun generateGroups(): PickerComponent.Data<StringContainer> {
    val list = listOf(
        PickerInfo(StringContainer("А"), "А"),
        PickerInfo(StringContainer("Б"), "Б"),
        PickerInfo(StringContainer("В"), "В"),
        PickerInfo(StringContainer("Г"), "Г"),
        PickerInfo(StringContainer("Д"), "Д"),
        PickerInfo(StringContainer("Е"), "Е"),
        PickerInfo(StringContainer("Ж"), "Ж"),
        PickerInfo(StringContainer("З"), "З"),
        PickerInfo(StringContainer("И"), "И"),
        PickerInfo(StringContainer("Й"), "Й"),
        PickerInfo(StringContainer("К"), "К"),
        PickerInfo(StringContainer("Л"), "Л"),
        PickerInfo(StringContainer("М"), "М"),
        PickerInfo(StringContainer("Н"), "Н"),
        PickerInfo(StringContainer("О"), "О"),
        PickerInfo(StringContainer("П"), "П"),
        PickerInfo(StringContainer("Р"), "Р"),
        PickerInfo(StringContainer("С"), "С"),
        PickerInfo(StringContainer("Т"), "Т"),
        PickerInfo(StringContainer("У"), "У"),
        PickerInfo(StringContainer("Ф"), "Ф"),
        PickerInfo(StringContainer("Х"), "Х"),
        PickerInfo(StringContainer("Ц"), "Ц"),
        PickerInfo(StringContainer("Ч"), "Ч"),
        PickerInfo(StringContainer("Ш"), "Ш"),
        PickerInfo(StringContainer("Щ"), "Щ"),
        PickerInfo(StringContainer("Ъ"), "Ъ"),
        PickerInfo(StringContainer("Ы"), "Ы"),
        PickerInfo(StringContainer("Ь"), "Ь"),
        PickerInfo(StringContainer("Э"), "Э"),
        PickerInfo(StringContainer("Ю"), "Ю"),
        PickerInfo(StringContainer("Я"), "Я"),
    )
    return PickerComponent.Data(
        setupData = list.first(),
        list = list,
    )
}