package com.mospolytech.features.schedule.main

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.mospolytech.features.base.utils.*
import com.mospolytech.features.schedule.model.DayUiModel
import com.mospolytech.features.schedule.model.WeekUiModel
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalPagerApi::class)
@Composable
fun DaysPager(
    weeks: List<WeekUiModel>,
    dayOfWeekPos: Int,
    pagerState: PagerState
) {
    HorizontalPager(
        count = weeks.size,
        state = pagerState,
        modifier = Modifier
            .fillMaxWidth()
            .height(85.dp)
            .disabledHorizontalPointerInputScroll(),
        flingBehavior = object : FlingBehavior {
            override suspend fun ScrollScope.performFling(initialVelocity: Float): Float {
                return initialVelocity
            }
        }
    ) {
        val week by remember(weeks, it) { mutableStateOf(weeks[it]) }
        WeekContent(
            week,
            dayOfWeekPos
        )
    }
}

@Composable
fun WeekContent(
    week: WeekUiModel,
    dayOfWeekPos: Int
) {
    val lazyRowState = rememberLazyListState(dayOfWeekPos)

    LaunchedEffect(dayOfWeekPos) {
        if (!lazyRowState.isItemFullyVisible(dayOfWeekPos)) {
            lazyRowState.animateScrollToItem(dayOfWeekPos)
        }
    }

    Row(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        week.days.forEachIndexed { index, day ->
            DayContent(day, dayOfWeekPos == index)
        }
    }
}

private val weekFormat = DateTimeFormatter.ofPattern("EEE")

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun DayContent(
    day: DayUiModel,
    isSelected: Boolean
) {
    val colorFrom = MaterialTheme3.colorScheme.secondary

    val backgroundColor = if (day.isToday)
        MaterialTheme3.colorScheme.secondaryContainer
    else
        MaterialTheme3.colorScheme.surface

    val borderColor by animateColorAsState(
        if (isSelected) colorFrom else Color.Transparent,
        tween(500)
    )

    val border = BorderStroke(1.dp, borderColor)
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        WithContentAlpha(ContentAlpha.medium) {
            Text(
                text = weekFormat.format(day.date).uppercase(),
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                overflow = TextOverflow.Clip,
                textAlign = TextAlign.Center
            )
        }
        Spacer(Modifier.height(3.5.dp))
        Card(
            modifier = Modifier
                .padding(start = 3.dp, end = 3.dp, top = 1.dp, bottom = 1.dp)
                .width(40.dp)
                .height(40.dp),
            shape = CircleShape,
            border = border,
            backgroundColor = backgroundColor
        ) {
            Box(Modifier.padding(start = 3.dp, end = 3.dp)) {
                Text(
                    text = day.date.dayOfMonth.toString(),
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Clip,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(bottom = 1.dp)
                )
            }
        }
        Spacer(Modifier.height(4.dp))
        Row(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .height(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            (0 until day.lessonCount).forEach {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 1.5.dp)
                        .size(3.5.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                )
            }
        }
    }
}