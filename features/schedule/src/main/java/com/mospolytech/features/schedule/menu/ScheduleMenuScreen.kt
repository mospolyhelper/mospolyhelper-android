package com.mospolytech.features.schedule.menu

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mospolytech.features.base.utils.*
import com.mospolytech.features.schedule.R
import com.mospolytech.features.schedule.menu.cards.*
import org.koin.androidx.compose.getViewModel

@Composable
fun ScheduleMenuScreen(viewModel: ScheduleMenuViewModel = getViewModel()) {
    val state by viewModel.state.collectAsState()

    ScheduleMenuContent(
        state = state,
        onScheduleClick = viewModel::onScheduleClick,
        onLessonsReviewClick = viewModel::onLessonsReviewClick,
        onScheduleCalendarClick = viewModel::onScheduleCalendarClick,
        onScheduleSourceClick = viewModel::onScheduleSourceClick,
        onFreePlaceClick = viewModel::onFreePlaceClick
    )
}

@Composable
fun ScheduleMenuContent(
    state: ScheduleMenuState,
    onScheduleClick: () -> Unit = { },
    onLessonsReviewClick: () -> Unit = { },
    onScheduleCalendarClick: () -> Unit = { },
    onScheduleSourceClick: () -> Unit = { },
    onFreePlaceClick: () -> Unit = { },
) {
    Column(
        Modifier
            .padding(top = 20.dp, start = 4.dp, end = 4.dp)
            .fillMaxSize()
    ) {
        Text(
            text = stringResource(R.string.sch_schedule),
            style = MaterialTheme3.typography.headlineMedium,
            modifier = Modifier.padding(start = 16.dp)
        )
        Spacer(Modifier.height(20.dp))
        Row(
            Modifier
                .height(190.dp)
                .fillMaxWidth()
        ) {
            Column {
                ScheduleSourcesCard(onScheduleSourceClick)
                ScheduleCard(state.main, onScheduleClick)
            }
            CalendarCard(
                state.date,
                onScheduleCalendarClick
            )
        }
        Row(
            Modifier
                .height(100.dp)
                .fillMaxWidth()
        ) {
            LessonsReviewCard(onLessonsReviewClick)
            FindFreePlaceCard(onFreePlaceClick)
        }
    }
}