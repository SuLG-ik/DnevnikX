package ru.sulgik.schedule.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import ru.sulgik.schedule.mvi.ScheduleListHostStore
import ru.sulgik.ui.core.AnimatedContentWithPlaceholder
import ru.sulgik.ui.core.defaultPlaceholder
import ru.sulgik.ui.core.optionalBackNavigationIcon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleListHostScreen(
    savedClasses: ScheduleListHostStore.State.SavedClasses,
    onSelectClass: () -> Unit,
    backAvailable: Boolean,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    scheduleContent: @Composable () -> Unit,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Расписание") },
                actions = {
                    AnimatedContentWithPlaceholder(
                        isLoading = savedClasses.isLoading,
                        state = savedClasses.data,
                        placeholderContent = {
                            Box(
                                Modifier
                                    .clip(CircleShape)
                                    .size(40.dp)
                                    .defaultPlaceholder()
                            )
                        }
                    ) {
                        IconButton(onClick = onSelectClass) {
                            Text(it.selectedClass.fullTitle)
                        }
                    }
                },
                navigationIcon = optionalBackNavigationIcon(backAvailable, onBack)
            )
        }
    ) {
        Box(
            modifier = Modifier.padding(it)
        ) {
            scheduleContent()
        }
    }
}