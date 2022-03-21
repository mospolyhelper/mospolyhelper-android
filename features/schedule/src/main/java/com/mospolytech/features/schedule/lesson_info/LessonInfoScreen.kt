package com.mospolytech.features.schedule.lesson_info

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.mospolytech.domain.schedule.model.group.Group
import com.mospolytech.domain.schedule.model.lesson.LessonDateTime
import com.mospolytech.domain.schedule.model.lesson.LessonInfo
import com.mospolytech.domain.schedule.model.place.Place
import com.mospolytech.domain.schedule.model.teacher.Teacher
import com.mospolytech.features.base.core.utils.*
import com.mospolytech.features.base.elements.InitialAvatar
import com.mospolytech.features.base.elements.PrimaryTopAppBar
import com.mospolytech.features.base.elements.SpacerHeight
import com.mospolytech.features.base.elements.SpacerWidth
import org.koin.androidx.compose.getViewModel
import java.time.format.DateTimeFormatter

@Composable
fun LessonInfoScreen(
    viewModel: LessonInfoViewModel = getViewModel(),
    lessonInfo: LessonInfo?
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.onLessonInfo(lessonInfo)
    }

    LessonInfoContent(
        state = state,
        onBackClick = viewModel::exit
    )
}

@Composable
private fun LessonInfoContent(
    state: LessonInfoState,
    onBackClick: ClickListener
) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Surface(
            color = MaterialTheme3.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp)
        ) {
            Column(Modifier.fillMaxWidth()) {
                PrimaryTopAppBar(
                    title = "",
                    onBackClick = onBackClick,
                    colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color.Transparent)
                )
                SpacerHeight(height = 32.dp)
                LessonType(
                    type = state.lessonInfo?.lesson?.type ?: ""
                )
                LessonTitle(
                    title = state.lessonInfo?.lesson?.title ?: ""
                )
                SpacerHeight(height = 4.dp)
                state.lessonInfo?.dateTime?.let { LessonDateTime(lessonDateTime = it) }
                SpacerHeight(height = 16.dp)
            }
        }
        SpacerHeight(height = 8.dp)
        LessonTeachers(
            teachers = state.lessonInfo?.lesson?.teachers ?: emptyList()
        )
        SpacerHeight(height = 14.dp)
        LessonPlaces(
            places = state.lessonInfo?.lesson?.places ?: emptyList()
        )
        SpacerHeight(height = 14.dp)
        LessonGroups(
            groups = state.lessonInfo?.lesson?.groups ?: emptyList()
        )
        SpacerHeight(height = 14.dp)
    }
}

@Composable
private fun LessonType(type: String) {
    WithContentAlpha(ContentAlpha.medium) {
        Text(
            text = type,
            style = MaterialTheme3.typography.titleSmall,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp)
        )
    }
}

@Composable
private fun LessonTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme3.typography.titleLarge,
        modifier = Modifier.padding(start = 16.dp, end = 16.dp)
    )
}

private val lessonDateFormat = DateTimeFormatter.ofPattern("d MMMM yyyy (EE)")
private val lessonTimeFormat = DateTimeFormatter.ofPattern("HH:mm")

@Composable
private fun LessonDateTime(lessonDateTime: LessonDateTime) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        WithContentAlpha(ContentAlpha.medium) {
            val timeStart = lessonDateTime.time.startTime.format(lessonTimeFormat)
            val timeEnd = lessonDateTime.time.endTime.format(lessonTimeFormat)
            val date = lessonDateTime.date.format(lessonDateFormat)
            Row(
                Modifier.padding(start = 16.dp, end = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(FluentIcons.ic_fluent_clock_16_regular),
                    contentDescription = null
                )
                SpacerWidth(3.dp)
                Text(
                    text = "$timeStart - $timeEnd",
                    style = MaterialTheme3.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 1.dp)
                )
            }
            Row(
                Modifier.padding(start = 8.dp, end = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(FluentIcons.ic_fluent_calendar_ltr_16_regular),
                    contentDescription = null
                )
                SpacerWidth(3.dp)
                Text(
                    text = date,
                    style = MaterialTheme3.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 1.dp)
                )
            }
        }
    }
}

@Composable
private fun LessonTeachers(teachers: List<Teacher>) {
    WithContentAlpha(ContentAlpha.medium) {
        Row(
            Modifier.padding(start = 16.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(FluentIcons.ic_fluent_hat_graduation_20_regular),
                contentDescription = null
            )
            SpacerWidth(4.dp)
            Text(
                text = "Преподаватели",
                style = MaterialTheme3.typography.titleSmall,
                modifier = Modifier.padding(bottom = 2.dp)
            )
        }
    }
    SpacerHeight(1.dp)
    teachers.forEachIndexed { index, teacher ->
        LessonItem(
            imageUrl = "",
            imageInitials = teacher.name
                .split(' ')
                .joinToString(separator = "") { it.take(1) },
            title = teacher.name,
            description = "Информация о преподавателе",
            onItemClick = {}
        )
        if (index != teachers.size - 1) {
            Divider(Modifier.padding(start = 70.dp, end = 20.dp))
        }
    }
}

@Composable
private fun LessonPlaces(places: List<Place>) {
    WithContentAlpha(ContentAlpha.medium) {
        Row(
            Modifier.padding(start = 16.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(FluentIcons.ic_fluent_location_20_regular),
                contentDescription = null
            )
            SpacerWidth(4.dp)
            Text(
                text = "Места",
                style = MaterialTheme3.typography.titleSmall,
                modifier = Modifier.padding(bottom = 2.dp)
            )
        }
    }
    SpacerHeight(1.dp)
    places.forEachIndexed { index, place ->
        LessonItem(
            imageUrl = "",
            imageInitials = place.title
                .split(' ')
                .joinToString(separator = "") { it.take(1) },
            title = place.title,
            description = place.description,
            onItemClick = {}
        )
        if (index != places.size - 1) {
            Divider(Modifier.padding(start = 70.dp, end = 20.dp))
        }
    }
}

@Composable
private fun LessonGroups(groups: List<Group>) {
    WithContentAlpha(ContentAlpha.medium) {
        Row(
            Modifier.padding(start = 16.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(FluentIcons.ic_fluent_people_20_regular),
                contentDescription = null
            )
            SpacerWidth(4.dp)
            Text(
                text = "Группы",
                style = MaterialTheme3.typography.titleSmall,
                modifier = Modifier.padding(bottom = 2.dp)
            )
        }
    }
    SpacerHeight(1.dp)
    groups.forEachIndexed { index, group ->
        LessonItem(
            imageUrl = "",
            imageInitials = group.title
                .split(' ')
                .joinToString(separator = "") { it.take(1) },
            title = group.title,
            description = "Информация о группе",
            onItemClick = {}
        )
        if (index != groups.size - 1) {
            Divider(Modifier.padding(start = 70.dp, end = 20.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LessonItem(
    imageUrl: String,
    imageInitials: String = "",
    title: String,
    description: String,
    onItemClick: ClickListener
) {
    Surface(
        modifier = Modifier
            .padding(start = 10.dp, end = 10.dp)
            .fillMaxWidth(),
        onClick = onItemClick,
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(start = 6.dp, end = 6.dp, top = 6.dp, bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            InitialAvatar(
                url = imageUrl,
                initials = imageInitials
            )
            SpacerWidth(8.dp)
            Column(Modifier.padding(bottom = 4.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme3.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                WithContentAlpha(alpha = ContentAlpha.medium) {
                    Text(
                        text = description,
                        style = MaterialTheme3.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}