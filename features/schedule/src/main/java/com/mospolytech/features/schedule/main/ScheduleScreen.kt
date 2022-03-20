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
import com.mospolytech.domain.schedule.model.lesson.Lesson
import com.mospolytech.domain.schedule.model.lesson.LessonDateTime
import com.mospolytech.domain.schedule.model.schedule.LessonsByTime
import com.mospolytech.features.base.core.utils.*
import com.mospolytech.features.base.elements.PrimaryTopAppBar
import com.mospolytech.features.schedule.R
import org.koin.androidx.compose.getViewModel
import java.time.LocalDate

@Composable
fun ScheduleScreen(viewModel: ScheduleViewModel = getViewModel()) {
    val state by viewModel.state.collectAsState()

    if (state.schedule.isNotEmpty() || state.isPreloading) {
        ScheduleContent(
            state,
            viewModel::exit,
            viewModel::onFabClick,
            viewModel::onSchedulePosChanged,
            viewModel::onWeeksPosChanged,
            onDayClick = viewModel::onDayClick,
            onLessonClick = viewModel::onLessonClick
        )
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ScheduleContent(
    state: ScheduleState,
    onBackClick: ClickListener,
    onFabClick: ClickListener,
    onSchedulePosChanged: Typed1Listener<Int>,
    onWeeksPosChanged: Typed1Listener<Int>,
    onDayClick: Typed1Listener<LocalDate>,
    onLessonClick: Typed2Listener<Lesson, LessonDateTime>
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
                pagerState = weekPagerState,
                onDayClick = onDayClick
            )

            if (state.isPreloading) {
                ScheduleDayPlaceHolder()
            } else {
                val schedulePagerState = rememberPagerState(state.schedulePos)
                schedulePagerState.bindTo(state.schedulePos)
                schedulePagerState.onPageChanged { onSchedulePosChanged(it) }

                SchedulePager(
                    scheduleDays = state.schedule,
                    pagerState = schedulePagerState,
                    onLessonClick = onLessonClick
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