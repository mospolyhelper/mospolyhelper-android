package com.mospolytech.features.schedule.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mospolytech.domain.schedule.model.group.Group
import com.mospolytech.domain.schedule.model.lesson.Lesson
import com.mospolytech.domain.schedule.model.place.Place
import com.mospolytech.domain.schedule.model.teacher.Teacher
import com.mospolytech.domain.schedule.utils.getShortName
import com.mospolytech.features.base.utils.ContentAlpha
import com.mospolytech.features.base.utils.WithContentAlpha
import com.mospolytech.features.base.view.placeholder
import com.mospolytech.features.schedule.R

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