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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.unit.dp
import com.google.accompanist.flowlayout.FlowRow
import com.mospolytech.domain.base.utils.capitalized
import com.mospolytech.domain.schedule.model.review.LessonReviewDay
import com.mospolytech.domain.schedule.model.review.LessonTimesReview
import com.mospolytech.features.base.utils.ContentAlpha
import com.mospolytech.features.base.utils.WithContentAlpha
import com.mospolytech.features.schedule.main.LessonHeader
import com.mospolytech.features.schedule.main.LessonTitle
import com.mospolytech.features.schedule.main.LessonType
import org.koin.androidx.compose.getViewModel
import java.time.DayOfWeek
import java.time.LocalDate
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
            lessonTimesReview.days.forEach { lessonTimesReviewByType ->
                Spacer(Modifier.height(10.dp))
                LessonType(lessonTimesReviewByType.lessonType)
                Spacer(Modifier.height(3.dp))
                FlowRow(
                    mainAxisSpacing = 19.dp,
                    crossAxisSpacing = 12.dp
                ) {
                    lessonTimesReviewByType.days.forEach { lessonReviewDay ->
                        RegularLessonTime(lessonReviewDay)
                    }
                }
            }
        }
    }
}

private val weekFormat = DateTimeFormatter.ofPattern("EEEE")
private val dateFormat = DateTimeFormatter.ofPattern("d MMM")

@Composable
fun RegularLessonTime(lessonTime: LessonReviewDay) {
    Column {
        WithContentAlpha(ContentAlpha.medium) {
            DateRange(
                dayOfWeek = lessonTime.dayOfWeek,
                dateFrom = lessonTime.dateFrom,
                dateTo = lessonTime.dateTo
            )
            Text(
                text = "${lessonTime.time.startTime} - ${lessonTime.time.endTime}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun DateRange(dayOfWeek: DayOfWeek, dateFrom: LocalDate, dateTo: LocalDate) {
    val dateRange = remember(dayOfWeek, dateFrom, dateTo) {
        if (dateFrom == dateTo)
            dateFormat.format(dateFrom)
        else
            dateFormat.format(dateFrom) + " - " + dateFormat.format(dateTo)
    }
    Text(
        text = weekFormat.format(dayOfWeek).capitalized() + "\n" + dateRange,
        style = MaterialTheme.typography.bodySmall
    )
}