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
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.mospolytech.features.base.core.utils.*
import com.mospolytech.features.schedule.model.DayUiModel
import com.mospolytech.features.schedule.model.WeekUiModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalPagerApi::class)
@Composable
fun DaysPager(
    weeks: List<WeekUiModel>,
    dayOfWeekPos: Int,
    pagerState: PagerState,
    onDayClick: Typed1Listener<LocalDate>
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
            dayOfWeekPos,
            onDayClick = onDayClick
        )
    }
}

@Composable
fun WeekContent(
    week: WeekUiModel,
    dayOfWeekPos: Int,
    onDayClick: Typed1Listener<LocalDate>
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
            DayContent(
                day,
                dayOfWeekPos == index,
                onDayClick = onDayClick
            )
        }
    }
}

private val weekFormat = DateTimeFormatter.ofPattern("EEE")

@OptIn(ExperimentalAnimationApi::class, androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun RowScope.DayContent(
    day: DayUiModel,
    isSelected: Boolean,
    onDayClick: Typed1Listener<LocalDate>
) {
    val colorFrom = MaterialTheme3.colorScheme.secondary

    val backgroundColor = if (day.isToday)
        MaterialTheme3.colorScheme.secondaryContainer
    else
        MaterialTheme3.colorScheme.surfaceVariant

    val borderColor by animateColorAsState(
        if (isSelected) colorFrom else Color.Transparent,
        tween(250)
    )

    val border = BorderStroke(1.dp, borderColor)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.weight(1f)
    ) {
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
            onClick = { onDayClick(day.date) },
            modifier = Modifier
                .padding(start = 3.dp, end = 3.dp, top = 1.dp, bottom = 1.dp)
                .size(39.dp),
            shape = CircleShape,
            border = border,
            containerColor = backgroundColor
        ) {
            Box(Modifier.padding(start = 3.dp, end = 3.dp).fillMaxSize()) {
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
        Spacer(Modifier.height(2.5.dp))
        if (day.lessonCount < 6) {
            Row(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .height(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                (0 until day.lessonCount).forEach {
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 1.5.dp)
                            .size(4.5.dp)
                            .background(MaterialTheme.colorScheme.primary, CircleShape)
                    )
                }
            }
        } else {
            Surface(
                modifier = Modifier.height(14.dp),
                color = MaterialTheme.colorScheme.primary,
                shape = CircleShape
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        modifier =  Modifier.padding(bottom = 1.dp, start = 5.dp, end = 5.dp),
                        text = day.lessonCount.toString(),
                        style =  MaterialTheme3.typography.titleSmall,
                        fontSize = 9.0.dp.sp(),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}