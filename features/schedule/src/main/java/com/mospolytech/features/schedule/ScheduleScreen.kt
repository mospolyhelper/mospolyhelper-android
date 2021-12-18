package com.mospolytech.features.schedule

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.mospolytech.domain.schedule.model.Lesson
import com.mospolytech.domain.schedule.model.LessonTime
import com.mospolytech.domain.schedule.model.LessonsByTime
import com.mospolytech.domain.schedule.model.ScheduleDay
import com.mospolytech.features.base.utils.ContentAlpha
import com.mospolytech.features.base.utils.LocalContentAlpha
import com.mospolytech.features.base.utils.WithContentAlpha
import com.mospolytech.features.base.utils.withAlpha
import org.koin.androidx.compose.getViewModel
import java.time.LocalDate

@Composable
fun ScheduleScreen(viewModel: ScheduleViewModel = getViewModel()) {
    val state by viewModel.state.collectAsState()

    if (state.schedule.isNotEmpty()) {
        ScheduleContent(state)
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ScheduleContent(state: ScheduleState) {
    val today = remember(state.schedule) {
        val now = LocalDate.now()
        state.schedule.indexOfFirst { it.date == now }.coerceAtLeast(0)
    }
    val pagerState = rememberPagerState(today)
    val selectedDate = remember(pagerState.currentPage) {
        state.schedule.getOrNull(pagerState.currentPage)?.date ?: LocalDate.MIN
    }

    Column {
        DateContent(selectedDate)
        SchedulePager(
            scheduleDays = state.schedule,
            pagerState = pagerState
        )
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun SchedulePager(
    scheduleDays: List<ScheduleDay>,
    pagerState: PagerState
) {
    HorizontalPager(
        count = scheduleDays.size,
        state = pagerState
    ) {
        val scheduleDay by remember { mutableStateOf(scheduleDays[it]) }
        Column(modifier = Modifier.fillMaxSize()) {
            LessonList(scheduleDay.lessons)
        }
    }
}

@Composable
fun DateContent(date: LocalDate) {
    Text(date.toString())
}

@Composable
fun LessonList(lessons: List<LessonsByTime>) {
    LazyColumn {
        for (lessonsByTime in lessons) {
            LessonPlace(lessonsByTime = lessonsByTime)
        }
    }
}


fun LazyListScope.LessonPlace(lessonsByTime: LessonsByTime) {
    item {
        LessonTimeContent(lessonsByTime.time)
    }
    items(lessonsByTime.lessons) { lesson ->
        LessonContent(lesson = lesson)
    }
}

@Composable
fun LessonTimeContent(lessonTime: LessonTime) {
    Text(
        text = "${lessonTime.startTime} - ${lessonTime.endTime}",
        style = MaterialTheme.typography.titleSmall,
        modifier = Modifier.padding(horizontal = 20.dp)
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LessonContent(lesson: Lesson, onItemClick: () -> Unit = { }) {
    Card(
        elevation = 4.dp,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
        onClick = onItemClick
    ) {
        Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp)) {
            WithContentAlpha(ContentAlpha.medium) {
                Text(
                    text = lesson.type,
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Text(
                text = lesson.title,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(4.dp))


            WithContentAlpha(ContentAlpha.medium) {
                Row {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_fluent_hat_graduation_16_regular),
                        contentDescription = null,
                        modifier = Modifier.size(17.dp)
                            .align(Alignment.CenterVertically)
                    )
                    Spacer(Modifier.width(5.dp))
                    Text(
                        text = lesson.teachers.joinToString { it.name },
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.fillMaxWidth()
                            .align(Alignment.CenterVertically),
                    )
                }
                Spacer(Modifier.height(2.dp))
                Row {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_fluent_people_16_regular),
                        contentDescription = null,
                        modifier = Modifier.size(17.dp)
                            .align(Alignment.CenterVertically)
                    )
                    Spacer(Modifier.width(5.dp))
                    Text(
                        text = lesson.groups.joinToString { it.title },
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.fillMaxWidth()
                            .align(Alignment.CenterVertically),
                    )
                }
                Spacer(Modifier.height(2.dp))
                Row {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_fluent_location_16_regular),
                        contentDescription = null,
                        modifier = Modifier.size(17.dp)
                            .align(Alignment.CenterVertically)
                    )
                    Spacer(Modifier.width(5.dp))
                    Text(
                        text = lesson.places.joinToString { it.title },
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.fillMaxWidth()
                            .align(Alignment.CenterVertically),
                    )
                }
            }

        }
    }
}