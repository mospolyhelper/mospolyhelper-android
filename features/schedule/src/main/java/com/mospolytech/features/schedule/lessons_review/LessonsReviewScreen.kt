package com.mospolytech.features.schedule.lessons_review

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.unit.dp
import com.google.accompanist.flowlayout.FlowRow
import com.mospolytech.domain.base.utils.capitalized
import com.mospolytech.domain.schedule.model.LessonReviewDay
import com.mospolytech.domain.schedule.model.LessonTimesReview
import com.mospolytech.features.base.utils.ContentAlpha
import com.mospolytech.features.base.utils.WithContentAlpha
import com.mospolytech.features.schedule.main.LessonHeader
import com.mospolytech.features.schedule.main.LessonTitle
import com.mospolytech.features.schedule.main.LessonType
import org.koin.androidx.compose.getViewModel
import java.time.format.DateTimeFormatter
import java.util.*

@Composable
fun LessonsReviewScreen(
    viewModel: LessonsReviewViewModel = getViewModel()
) {
    val state by viewModel.state.collectAsState()

    LessonsReviewContent(state.lessons)
}

@Composable
fun LessonsReviewContent(lessons: List<LessonTimesReview>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(lessons) { lessonTimesReview ->
            LessonTimesReviewContent(lessonTimesReview)
        }
    }
}

@Composable
fun LessonTimesReviewContent(lessonTimesReview: LessonTimesReview) {
    Card(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 5.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .fillMaxWidth()
        ) {
            LessonTitle(lessonTimesReview.lessonTitle)
            Spacer(Modifier.height(10.dp))
            FlowRow(
                mainAxisSpacing = 19.dp,
                crossAxisSpacing = 12.dp
            ) {
                lessonTimesReview.days.forEach { lessonReviewDay ->
                    when (lessonReviewDay) {
                        is LessonReviewDay.Regular -> RegularLessonTime(lessonReviewDay)
                        is LessonReviewDay.Single -> SingleLessonTime(lessonReviewDay)
                    }
                }
            }
        }
    }
}

private val weekFormat = DateTimeFormatter.ofPattern("EEE")
private val dateFormat = DateTimeFormatter.ofPattern("d MMM")
private val singleDateFormat = DateTimeFormatter.ofPattern("EEE, d MMM")

@Composable
fun RegularLessonTime(lessonTime: LessonReviewDay.Regular) {
    Column {
        LessonType(lessonTime.lessonType)
        Spacer(Modifier.height(3.dp))
        WithContentAlpha(ContentAlpha.medium) {
            Text(
                text = weekFormat.format(lessonTime.dayOfWeek)
                    .capitalized() + ", " + dateFormat.format(lessonTime.dateFrom) + " - " + dateFormat.format(lessonTime.dateTo),
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "${lessonTime.time.startTime} - ${lessonTime.time.endTime}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun SingleLessonTime(lessonTime: LessonReviewDay.Single) {
    Column {
        LessonType(lessonTime.lessonType)
        Spacer(Modifier.height(3.dp))
        WithContentAlpha(ContentAlpha.medium) {
            Text(
                text = singleDateFormat.format(lessonTime.date).capitalized(),
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "${lessonTime.time.startTime} - ${lessonTime.time.endTime}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}