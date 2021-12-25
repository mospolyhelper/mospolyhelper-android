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
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mospolytech.features.base.utils.ContentAlpha
import com.mospolytech.features.base.utils.WithContentAlpha
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ScheduleMenuContent(
    onScheduleClick: () -> Unit = { },
    onLessonsReviewClick: () -> Unit = { },
    onScheduleCalendarClick: () -> Unit = { },
    onScheduleSourceClick: () -> Unit = { },
    onFreePlaceClick: () -> Unit = { },
) {
    Column(Modifier.padding(top = 10.dp).fillMaxSize()) {
        Row(
            Modifier
                .height(100.dp)
                .fillMaxWidth()
        ) {
            Card(
                modifier = Modifier
                    .padding(horizontal = 5.dp, vertical = 5.dp)
                    .fillMaxHeight()
                    .fillMaxWidth(0.6f)
                    .clickable(onClick = onScheduleSourceClick),
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(Modifier.padding(horizontal = 15.dp, vertical = 10.dp)) {
                    Text(text = "Выберите расписание")
                }
            }
        }
        Row(
            Modifier
                .height(150.dp)
                .fillMaxWidth()
        ) {
            Card(
                modifier = Modifier
                    .padding(horizontal = 5.dp, vertical = 5.dp)
                    .fillMaxHeight()
                    .fillMaxWidth(0.6f)
                    .clickable(onClick = onScheduleClick),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(Modifier.padding(horizontal = 15.dp, vertical = 10.dp)) {
                    Text(text = "Расписание")
                    WithContentAlpha(alpha = ContentAlpha.medium) {
                        Text(text = "Сейчас\nИнформационные системы и технологии")
                    }
                }
            }

            Card(
                modifier = Modifier
                    .padding(horizontal = 5.dp, vertical = 5.dp)
                    .fillMaxHeight()
                    .fillMaxWidth()
                    .clickable(onClick = onScheduleCalendarClick),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(Modifier.padding(horizontal = 15.dp, vertical = 10.dp)) {
                    Text(text = "Календарь")
                    WithContentAlpha(alpha = ContentAlpha.medium) {
                        Text(text = "Сегодня\n10 декабря\nПятница")
                    }
                }
            }
        }
        Row(
            Modifier
                .height(100.dp)
                .fillMaxWidth()
        ) {
            Card(
                modifier = Modifier
                    .padding(horizontal = 5.dp, vertical = 5.dp)
                    .fillMaxHeight()
                    .fillMaxWidth(0.5f)
                    .clickable(onClick = onLessonsReviewClick),
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(Modifier.padding(horizontal = 15.dp, vertical = 10.dp)) {
                    Text(text = "Обзор предметов")
                }
            }
            Card(
                modifier = Modifier
                    .padding(horizontal = 5.dp, vertical = 5.dp)
                    .fillMaxHeight()
                    .fillMaxWidth()
                    .clickable(onClick = onFreePlaceClick),
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(Modifier.padding(horizontal = 15.dp, vertical = 10.dp)) {
                    Text(text = "Найти свободную аудиторию")
                }
            }
        }
    }
}