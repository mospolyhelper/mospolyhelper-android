package com.mospolytech.features.schedule.main

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState
import com.mospolytech.features.base.utils.*
import com.mospolytech.features.base.view.PrimaryTopAppBar
import com.mospolytech.features.schedule.R
import org.koin.androidx.compose.getViewModel

@Composable
fun ScheduleScreen(viewModel: ScheduleViewModel = getViewModel()) {
    val state by viewModel.state.collectAsState()

    if (state.schedule.isNotEmpty() || state.isPreloading) {
        ScheduleContent(
            state,
            viewModel::exit,
            viewModel::onFabClick,
            viewModel::onSchedulePosChanged,
            viewModel::onWeeksPosChanged
        )
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ScheduleContent(
    state: ScheduleState,
    onBackClick: ClickListener,
    onFabClick: ClickListener,
    onSchedulePosChanged: TypedListener<Int>,
    onWeeksPosChanged: TypedListener<Int>
) {
    Box {
        Column(Modifier.fillMaxSize()) {
            val weekPagerState = rememberPagerState(state.weeksPos)
            weekPagerState.bindTo(state.weeksPos)
            weekPagerState.onPageChanged { onWeeksPosChanged(it) }

            PrimaryTopAppBar(
                title = stringResource(R.string.sch_schedule),
                showLoading = state.isLoading,
                onBackClick = onBackClick
            )

            DaysPager(
                weeks = state.weeks,
                dayOfWeekPos = state.dayOfWeekPos,
                pagerState = weekPagerState
            )

            if (state.isPreloading) {
                ScheduleDayPlaceHolder()
            } else {
                val schedulePagerState = rememberPagerState(state.schedulePos)
                schedulePagerState.bindTo(state.schedulePos)
                schedulePagerState.onPageChanged { onSchedulePosChanged(it) }

                SchedulePager(
                    scheduleDays = state.schedule,
                    pagerState = schedulePagerState
                )
            }
        }
        Fab(state.showBackToTodayFab, onFabClick)
    }
}

@Composable
fun BoxScope.Fab(isVisible: Boolean, onClick: () -> Unit) {
    AnimatedVisibility(
        visible = isVisible,
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(24.dp),
        enter = fadeIn() + slideInVertically { it / 2 },
        exit = slideOutVertically { it / 2 } + fadeOut(),
    ) {
        ExtendedFloatingActionButton(
            onClick = onClick,
            containerColor = MaterialTheme.colorScheme.primary,
            text = {
                Text(text = stringResource(R.string.sch_to_today))
            },
            icon = {
                Icon(
                    painter = painterResource(FluentIcons.ic_fluent_calendar_today_24_regular),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }
        )
    }
}