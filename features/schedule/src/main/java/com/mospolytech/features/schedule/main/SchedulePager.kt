package com.mospolytech.features.schedule.main

import androidx.annotation.RawRes
import androidx.annotation.XmlRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.mospolytech.domain.schedule.model.group.Group
import com.mospolytech.domain.schedule.model.lesson.Lesson
import com.mospolytech.domain.schedule.model.lesson.LessonTime
import com.mospolytech.domain.schedule.model.place.Place
import com.mospolytech.domain.schedule.model.schedule.LessonsByTime
import com.mospolytech.domain.schedule.model.schedule.ScheduleDay
import com.mospolytech.domain.schedule.model.teacher.Teacher
import com.mospolytech.features.base.view.placeholder
import com.mospolytech.features.schedule.R
import java.time.LocalDate
import java.time.LocalTime

private val relaxAnims = listOf(
    R.raw.sch_relax_0,
    R.raw.sch_relax_1,
    R.raw.sch_relax_2
)

@OptIn(ExperimentalPagerApi::class)
@Composable
fun SchedulePager(
    scheduleDays: List<ScheduleDay>,
    pagerState: PagerState
) {
    HorizontalPager(
        count = scheduleDays.size,
        state = pagerState,
        modifier = Modifier.fillMaxSize()
    ) {
        val scheduleDay by remember(scheduleDays, it) { mutableStateOf(scheduleDays[it]) }

        if (scheduleDay.lessons.isNotEmpty()) {
            LessonList(scheduleDay.lessons)
        } else {
            val randomAnim = remember { relaxAnims[it % relaxAnims.size] }
            NoLessonsDay(randomAnim)
        }
    }
}

@Composable
fun NoLessonsDay(@RawRes animation: Int) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(animation))
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
            text = stringResource(R.string.sch_no_lessons_today),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .weight(4f)
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 10.dp),
            softWrap = true,
            textAlign = TextAlign.Center
        )
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

@Composable
fun ScheduleDayPlaceHolder() {
    val lessons = List(3) {
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

    LessonList(lessons = lessons, isLoading = true)
}