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
import com.mospolytech.features.base.utils.MaterialTheme3
import com.mospolytech.features.base.utils.disabledHorizontalPointerInputScroll
import com.mospolytech.features.base.utils.isItemFullyVisible
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
            .height(100.dp)
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

    LazyRow(
        modifier = Modifier
            .fillMaxWidth(),
        state = lazyRowState,
        contentPadding = PaddingValues(horizontal = 6.dp)
    ) {
        itemsIndexed(week.days) { index, day ->
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

    val borderColor by animateColorAsState(
        if (isSelected) colorFrom else Color.Transparent,
        tween(500)
    )

    val border = BorderStroke(1.dp, borderColor)
    Card(
        modifier = Modifier
            .padding(start = 3.dp, end = 3.dp, top = 5.dp, bottom = 5.dp)
            .width(60.dp)
            .height(70.dp),
        shape = RoundedCornerShape(20.dp),
        border = border
    ) {
        Column(
            modifier = Modifier
                .padding(start = 5.dp, end = 5.dp, top = 10.dp)
        ) {
            Text(
                text = weekFormat.format(day.date).uppercase(),
                style = MaterialTheme.typography.labelMedium,
                maxLines = 1,
                overflow = TextOverflow.Clip,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = day.date.dayOfMonth.toString(),
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Clip,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(4.dp))
            Row(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                (0 until day.lessonCount).forEach {
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 2.dp)
                            .size(4.dp)
                            .background(MaterialTheme.colorScheme.primary, CircleShape)
                    )
                }
            }
        }
    }
}