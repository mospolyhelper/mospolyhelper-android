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
        onLessonsReviewClick = viewModel::onLessonsReviewClick
    )
}

@Composable
fun ScheduleMenuContent(
    onScheduleClick: () -> Unit = { },
    onLessonsReviewClick: () -> Unit = { }
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Button(onClick = { }) {
            Text(text = "Schedule sources")
        }
        Button(onClick = onScheduleClick) {
            Text(text = "Schedule")
        }
        Button(onClick = onLessonsReviewClick) {
            Text(text = "Lessons review")
        }
        Button(onClick = { }) {
            Text(text = "Calendar")
        }
        Button(onClick = { }) {
            Text(text = "Find free place")
        }
    }
}