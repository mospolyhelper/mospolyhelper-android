package com.mospolytech.features.schedule.lesson_info

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mospolytech.domain.schedule.model.group.Group
import com.mospolytech.domain.schedule.model.lesson.LessonInfo
import com.mospolytech.domain.schedule.model.place.Place
import com.mospolytech.domain.schedule.model.teacher.Teacher
import com.mospolytech.features.base.core.utils.ClickListener
import com.mospolytech.features.base.core.utils.ContentAlpha
import com.mospolytech.features.base.core.utils.MaterialTheme3
import com.mospolytech.features.base.core.utils.WithContentAlpha
import com.mospolytech.features.base.elements.InitialAvatar
import com.mospolytech.features.base.elements.PrimaryTopAppBar
import com.mospolytech.features.base.elements.SpacerHeight
import org.koin.androidx.compose.getViewModel

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
        PrimaryTopAppBar(
            title = "",
            onBackClick = onBackClick
        )
        SpacerHeight(height = 64.dp)
        LessonType(
            type = state.lessonInfo?.lesson?.type ?: ""
        )
        LessonTitle(
            title = state.lessonInfo?.lesson?.title ?: ""
        )
        SpacerHeight(height = 16.dp)
        LessonTeachers(
            teachers = state.lessonInfo?.lesson?.teachers ?: emptyList()
        )
        SpacerHeight(height = 16.dp)
        LessonPlaces(
            places = state.lessonInfo?.lesson?.places ?: emptyList()
        )
        SpacerHeight(height = 16.dp)
        LessonGroups(
            groups = state.lessonInfo?.lesson?.groups ?: emptyList()
        )


        Text(text = state.lessonInfo?.dateTime.toString())
    }
}

@Composable
private fun LessonType(type: String) {
    Text(
        text = type,
        style = MaterialTheme3.typography.titleSmall,
        modifier = Modifier.padding(start = 16.dp, end = 16.dp)
    )
}

@Composable
private fun LessonTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme3.typography.titleLarge,
        modifier = Modifier.padding(start = 16.dp, end = 16.dp)
    )
}

@Composable
private fun LessonTeachers(teachers: List<Teacher>) {
    Text(
        text = "Преподаватели",
        style = MaterialTheme3.typography.titleSmall,
        modifier = Modifier.padding(start = 16.dp, end = 16.dp)
    )
    SpacerHeight(6.dp)
    teachers.forEachIndexed { index, teacher ->
        TeacherItem(
            teacher = teacher,
            onItemClick = {}
        )
        if (index != teachers.size - 1) {
            SpacerHeight(4.dp)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TeacherItem(
    teacher: Teacher,
    onItemClick: ClickListener
) {
    Surface(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp)
            .fillMaxWidth(),
        onClick = onItemClick
    ) {
        Row(Modifier.fillMaxWidth()) {
            InitialAvatar(
                url = "",
                initials = teacher.name.split(' ')
                    .joinToString(separator = "") { it.take(1) }
            )
            Spacer(Modifier.width(5.dp))
            Column {
                Text(
                    text = teacher.name,
                    style = MaterialTheme3.typography.titleSmall
                )
                WithContentAlpha(alpha = ContentAlpha.medium) {
                    Text(
                        text = "Информация о преподавателе",
                        style = MaterialTheme3.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
private fun LessonPlaces(places: List<Place>) {
    Text(
        text = "Места",
        style = MaterialTheme3.typography.titleSmall,
        modifier = Modifier.padding(start = 16.dp, end = 16.dp)
    )
    SpacerHeight(6.dp)
    places.forEachIndexed { index, place ->
        LessonPlace(
            place = place,
            onItemClick = {}
        )
        if (index != places.size - 1) {
            SpacerHeight(4.dp)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LessonPlace(
    place: Place,
    onItemClick: ClickListener
) {
    Surface(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp)
            .fillMaxWidth(),
        onClick = onItemClick
    ) {
        Row(Modifier.fillMaxWidth()) {
            InitialAvatar(
                url = "",
                initials = place.title.split(' ')
                    .joinToString(separator = "") { it.take(1) }
            )
            Spacer(Modifier.width(5.dp))
            Column {
                Text(
                    text = place.title,
                    style = MaterialTheme3.typography.titleSmall
                )
                WithContentAlpha(alpha = ContentAlpha.medium) {
                    Text(
                        text = place.description,
                        style = MaterialTheme3.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
private fun LessonGroups(groups: List<Group>) {
    Text(
        text = "Группы",
        style = MaterialTheme3.typography.titleSmall,
        modifier = Modifier.padding(start = 16.dp, end = 16.dp)
    )
    SpacerHeight(6.dp)
    groups.forEachIndexed { index, group ->
        LessonGroup(
            group = group,
            onItemClick = {}
        )
        if (index != groups.size - 1) {
            SpacerHeight(4.dp)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LessonGroup(
    group: Group,
    onItemClick: ClickListener
) {
    Surface(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp)
            .fillMaxWidth(),
        onClick = onItemClick
    ) {
        Row(Modifier.fillMaxWidth()) {
            InitialAvatar(
                url = "",
                initials = group.title.split(' ')
                    .joinToString(separator = "") { it.take(1) }
            )
            Spacer(Modifier.width(5.dp))
            Column {
                Text(
                    text = group.title,
                    style = MaterialTheme3.typography.titleSmall
                )
                WithContentAlpha(alpha = ContentAlpha.medium) {
                    Text(
                        text = "Информация о группе",
                        style = MaterialTheme3.typography.bodySmall
                    )
                }
            }
        }
    }
}