package com.mospolytech.features.schedule.menu

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.getViewModel

@Composable
fun ScheduleMenuScreen(viewModel: ScheduleMenuViewModel = getViewModel()) {
    ScheduleMenuContent(
        onScheduleClick = viewModel::onScheduleClick,
        onLessonsReviewClick = viewModel::onLessonsReviewClick,
        onScheduleCalendarClick = viewModel::onScheduleCalendarClick,
        onScheduleSourceClick = viewModel::onScheduleSourceClick,
        onFreePlaceClick = viewModel::onFreePlaceClick
    )
}

@Composable
fun ScheduleMenuContent(
    onScheduleClick: () -> Unit = { },
    onLessonsReviewClick: () -> Unit = { },
    onScheduleCalendarClick: () -> Unit = { },
    onScheduleSourceClick: () -> Unit = { },
    onFreePlaceClick: () -> Unit = { },
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Button(onClick = onScheduleSourceClick) {
            Column {
                Text(text = "Schedule sources")
                Text(text = "181-721")
            }
        }
        Button(onClick = onScheduleClick) {
            Text(text = "Schedule")
        }
        Button(onClick = onLessonsReviewClick) {
            Text(text = "Lessons review")
        }
        Button(onClick = onScheduleCalendarClick) {
            Text(text = "Calendar")
        }
        Button(onClick = onFreePlaceClick) {
            Text(text = "Find free place")
        }
    }
}