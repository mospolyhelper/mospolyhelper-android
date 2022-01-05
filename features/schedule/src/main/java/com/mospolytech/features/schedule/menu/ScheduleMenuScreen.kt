package com.mospolytech.features.schedule.menu

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.GridItemSpan
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mospolytech.features.base.utils.*
import com.mospolytech.features.schedule.R
import org.koin.androidx.compose.getViewModel

@Composable
fun ScheduleMenuScreen(viewModel: ScheduleMenuViewModel = getViewModel()) {
    ScheduleMenuContent(
        onScheduleClick = viewModel::onScheduleClick,
        onLessonsReviewClick = viewModel::onLessonsReviewClick,
        onScheduleCalendarClick = viewModel::onScheduleCalendarClick,
        onScheduleSourceClick = viewModel::onScheduleSourceClick,
        onFreePlaceClick = viewModel::onFreePlaceClick
    )
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun ScheduleMenuContent(
    onScheduleClick: () -> Unit = { },
    onLessonsReviewClick: () -> Unit = { },
    onScheduleCalendarClick: () -> Unit = { },
    onScheduleSourceClick: () -> Unit = { },
    onFreePlaceClick: () -> Unit = { },
) {
    Column(
        Modifier
            .padding(top = 20.dp, start = 4.dp, end = 4.dp)
            .fillMaxSize()
    ) {
        Text(
            text = stringResource(R.string.sch_schedule),
            style = MaterialTheme3.typography.headlineMedium,
            modifier = Modifier.padding(start = 16.dp)
        )
        Spacer(Modifier.height(20.dp))
        Row(
            Modifier
                .height(190.dp)
                .fillMaxWidth()
        ) {
            Column {
                ScheduleSourcesCard(onScheduleSourceClick)
                ScheduleCard(onScheduleClick)
            }
            CalendarCard(onScheduleCalendarClick)
        }
        Row(
            Modifier
                .height(100.dp)
                .fillMaxWidth()
        ) {
            LessonsReviewCard(onLessonsReviewClick)
            FindFreePlaceCard(onFreePlaceClick)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ScheduleSourcesCard(
    onScheduleSourceClick: ClickListener
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 4.dp, vertical = 4.dp)
            .height(70.dp)
            .fillMaxWidth(0.6f),
        shape = RoundedCornerShape(16.dp),
        onClick = onScheduleSourceClick
    ) {
        Box(Modifier.padding(horizontal = 15.dp, vertical = 10.dp)) {
            Text(text = "Выберите расписание")
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ScheduleCard(
    onScheduleClick: ClickListener
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 4.dp, vertical = 4.dp)
            .height(120.dp)
            .fillMaxWidth(0.6f),
        shape = RoundedCornerShape(16.dp),
        onClick = onScheduleClick
    ) {
        Column(Modifier.padding(horizontal = 15.dp, vertical = 10.dp)) {
            Text(text = stringResource(R.string.sch_schedule))
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Сейчас".uppercase(),
                style = MaterialTheme3.typography.labelSmall,
                color = MaterialTheme3.colorScheme.secondary
            )
            WithContentAlpha(alpha = ContentAlpha.medium) {
                Text(
                    text = "Информационные системы и технологии",
                    style = MaterialTheme3.typography.bodySmall
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CalendarCard(
    onScheduleCalendarClick: ClickListener
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 4.dp, vertical = 4.dp)
            .height(150.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        onClick = onScheduleCalendarClick
    ) {
        Column(
            Modifier
                .padding(horizontal = 15.dp, vertical = 10.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "Календарь",
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally),
                textAlign = TextAlign.Center
            )
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column {
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "Пятница".uppercase(),
                        style = MaterialTheme3.typography.labelMedium,
                        color = MaterialTheme3.colorScheme.tertiary,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "10",
                        style = MaterialTheme3.typography.displayMedium,
                        color = MaterialTheme3.colorScheme.primary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(y = -MaterialTheme3.typography.displayMedium.fontSize.dp() * 0.2f)
                            .height(MaterialTheme3.typography.displayMedium.fontSize.dp() * 1.2f),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "декабря",
                        style = MaterialTheme3.typography.bodySmall,
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(y = -MaterialTheme3.typography.displayMedium.fontSize.dp() * 0.2f) ,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LessonsReviewCard(
    onLessonsReviewClick: ClickListener
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 4.dp, vertical = 4.dp)
            .fillMaxHeight()
            .fillMaxWidth(0.5f),
        shape = RoundedCornerShape(16.dp),
        onClick = onLessonsReviewClick
    ) {
        Box(Modifier.padding(horizontal = 15.dp, vertical = 10.dp)) {
            Text(text = "Обзор предметов")
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FindFreePlaceCard(
    onFreePlaceClick: ClickListener
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 4.dp, vertical = 4.dp)
            .fillMaxHeight()
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        onClick = onFreePlaceClick
    ) {
        Box(Modifier.padding(horizontal = 15.dp, vertical = 10.dp)) {
            Text(text = "Найти свободную аудиторию")
        }
    }
}