package com.mospolytech.features.schedule.main

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.mospolytech.domain.schedule.model.group.Group
import com.mospolytech.domain.schedule.model.lesson.Lesson
import com.mospolytech.domain.schedule.model.lesson.LessonTime
import com.mospolytech.domain.schedule.model.place.Place
import com.mospolytech.domain.schedule.model.schedule.LessonsByTime
import com.mospolytech.domain.schedule.model.schedule.ScheduleDay
import com.mospolytech.domain.schedule.model.teacher.Teacher
import com.mospolytech.domain.schedule.utils.getShortName
import com.mospolytech.features.base.utils.*
import com.mospolytech.features.base.view.placeholder
import com.mospolytech.features.schedule.R
import com.mospolytech.features.schedule.model.DayUiModel
import com.mospolytech.features.schedule.model.WeekUiModel
import org.koin.androidx.compose.getViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun ScheduleScreen(viewModel: ScheduleViewModel = getViewModel()) {
    val state by viewModel.state.collectAsState()

    if (state.schedule.isNotEmpty() || state.isPreloading) {
        ScheduleContent(
            state,
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
    onFabClick: ClickListener,
    onSchedulePosChanged: TypedListener<Int>,
    onWeeksPosChanged: TypedListener<Int>
) {
    val schedulePagerState = rememberPagerState(state.schedulePos)
    schedulePagerState.bindTo(state.schedulePos)
    schedulePagerState.onPageChanged { onSchedulePosChanged(it) }

    val weekPagerState = rememberPagerState(state.weeksPos)
    weekPagerState.bindTo(state.weeksPos)
    weekPagerState.onPageChanged { onWeeksPosChanged(it) }


    Box {
        Column(Modifier.fillMaxSize()) {
            DaysPager(
                weeks = state.weeks,
                dayOfWeekPos = state.dayOfWeekPos,
                pagerState = weekPagerState
            )
            SchedulePager(
                scheduleDays = state.schedule,
                pagerState = schedulePagerState,
                isLoading = state.isPreloading
            )
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
                    painter = painterResource(R.drawable.ic_fluent_calendar_today_24_regular),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }
        )
    }
}

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

@OptIn(ExperimentalPagerApi::class)
@Composable
fun SchedulePager(
    scheduleDays: List<ScheduleDay>,
    pagerState: PagerState,
    isLoading: Boolean = false
) {
    val scheduleDays = remember(scheduleDays, isLoading) {
        if (isLoading) {
            listOf(
                ScheduleDay(
                    date = LocalDate.now(),
                    lessons = List(10) {
                        LessonsByTime(
                            time = LessonTime(LocalTime.now(), LocalTime.now()),
                            lessons = listOf(
                                Lesson(
                                    "",
                                    "Qwerty qwerty",
                                    listOf(Teacher("", "")),
                                    listOf(Group("", "")),
                                    listOf(Place("", "", ""))
                                )
                            )
                        )
                    }
                )
            )
        } else {
            scheduleDays
        }
    }
    HorizontalPager(
        count = scheduleDays.size,
        state = pagerState,
        modifier = Modifier.fillMaxSize()
    ) {
        val scheduleDay by remember(scheduleDays, it) { mutableStateOf(scheduleDays[it]) }
        if (scheduleDay.lessons.isNotEmpty()) {
            LessonList(scheduleDay.lessons, isLoading)
        } else {
            val relaxAnims = remember { listOf(
                R.raw.sch_relax_0,
                R.raw.sch_relax_1,
                R.raw.sch_relax_2
            ) }
            val randomAnim = remember { relaxAnims[it % relaxAnims.size] }
            val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(randomAnim))
            val progress by animateLottieCompositionAsState(
                composition,
                iterations = LottieConstants.IterateForever
            )
            Column {
                Spacer(modifier = Modifier.weight(1f))
                LottieAnimation(
                    composition,
                    progress,
                    modifier = Modifier
                        .weight(5f)
                )
                Text(
                    text = "There is no lessons today\nYou can spend your time on business or just relax",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .weight(4f)
                        .padding(horizontal = 24.dp, vertical = 10.dp),
                    softWrap = true,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun DateContent(date: LocalDate) {
    Text(date.toString())
}

@Composable
fun LessonList(lessons: List<LessonsByTime>, isLoading: Boolean = false) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        for (lessonsByTime in lessons) {
            LessonPlace(lessonsByTime = lessonsByTime, isLoading = isLoading)
        }
    }
}


fun LazyListScope.LessonPlace(lessonsByTime: LessonsByTime, isLoading: Boolean = false) {
    item {
        LessonTimeContent(lessonsByTime.time, isLoading)
    }
    items(lessonsByTime.lessons) { lesson ->
        LessonContent(lesson = lesson, isLoading = isLoading)
    }
}

@Composable
fun LessonTimeContent(lessonTime: LessonTime, isLoading: Boolean = false) {
    Text(
        text = "${lessonTime.startTime} - ${lessonTime.endTime}",
        style = MaterialTheme.typography.titleSmall,
        modifier = Modifier
            .padding(horizontal = 34.dp)
            .placeholder(visible = isLoading)
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LessonContent(lesson: Lesson, isLoading: Boolean = false, onItemClick: () -> Unit = { }) {
    Card(
        elevation = 4.dp,
        shape = RoundedCornerShape(25.dp),
        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
        onClick = onItemClick
    ) {
        Column(modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 15.dp, bottom = 18.dp)) {
            WithContentAlpha(ContentAlpha.medium) {
                LessonHeader(lesson.type, isLoading)
            }
            Spacer(Modifier.height(4.dp))
            LessonTitle(lesson.title, isLoading)
            Spacer(Modifier.height(6.dp))
            WithContentAlpha(ContentAlpha.medium) {
                TeachersContent(lesson.teachers, isLoading)
                Spacer(Modifier.height(4.dp))
                GroupsContent(lesson.groups, isLoading)
                Spacer(Modifier.height(4.dp))
                PlacesContent(lesson.places, isLoading)
            }
        }
    }
}

@Composable
fun LessonHeader(type: String, isLoading: Boolean = false) {
    LessonType(type, isLoading)
}

@Composable
fun LessonType(type: String, isLoading: Boolean = false) {
    Text(
        text = type.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.placeholder(visible = isLoading)
    )
}

@Composable
fun LessonTitle(title: String, isLoading: Boolean = false) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier
            .fillMaxWidth()
            .placeholder(visible = isLoading)
    )
}

@Composable
fun TeachersContent(teachers: List<Teacher>, isLoading: Boolean = false) {
    Row(
        modifier = Modifier.placeholder(visible = isLoading)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_fluent_hat_graduation_16_regular),
            contentDescription = null,
            modifier = Modifier
                .size(17.dp)
                .align(Alignment.CenterVertically)
        )
        Spacer(Modifier.width(5.dp))
        val teachersText = remember(teachers) {
            if (teachers.size == 1)
                teachers.first().name
            else
                teachers.joinToString { it.getShortName() }
        }
        Text(
            text = teachersText,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterVertically),
        )
    }
}

@Composable
fun GroupsContent(groups: List<Group>, isLoading: Boolean = false) {
    Row(
        modifier = Modifier.placeholder(visible = isLoading)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_fluent_people_16_regular),
            contentDescription = null,
            modifier = Modifier
                .size(17.dp)
                .align(Alignment.CenterVertically)
        )
        Spacer(Modifier.width(5.dp))
        val groupsText = remember(groups) { groups.joinToString { it.title } }
        Text(
            text = groupsText,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterVertically),
        )
    }
}

@Composable
fun PlacesContent(places: List<Place>, isLoading: Boolean = false) {
    Row(
        modifier = Modifier.placeholder(visible = isLoading)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_fluent_location_16_regular),
            contentDescription = null,
            modifier = Modifier
                .size(17.dp)
                .align(Alignment.CenterVertically)
        )
        Spacer(Modifier.width(5.dp))
        val placesText = remember(places) { places.joinToString { it.title } }
        Text(
            text = placesText,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterVertically),
        )
    }
}