package com.mospolytech.features.schedule.menu

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.GridItemSpan
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.material.Card
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
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
    LazyVerticalGrid(cells = GridCells.Fixed(3)) {
        item({ GridItemSpan(2) }) {
            Card(modifier = Modifier.height(100.dp)) {
                Button(onClick = onScheduleSourceClick) {
                    Column {
                        Text(text = "Schedule sources")
                        Text(text = "181-721")
                    }
                }
            }
        }
        item({ GridItemSpan(2) }) {
            Card(modifier = Modifier.height(200.dp)) {
                Button(onClick = onScheduleClick) {
                    Text(text = "Schedule")
                }
            }
        }
        item({ GridItemSpan(1) }) {
            Card(modifier = Modifier.height(100.dp)) {
                Button(onClick = onLessonsReviewClick) {
                    Text(text = "Lessons review")
                }
            }
        }
        item({ GridItemSpan(1) }) {
            Card(modifier = Modifier.height(100.dp)) {
                Button(onClick = onScheduleCalendarClick) {
                    Text(text = "Calendar")
                }
            }
        }
        item({ GridItemSpan(1) }) {
            Card(modifier = Modifier.height(100.dp)) {
                Button(onClick = onFreePlaceClick) {
                    Text(text = "Find free place")
                }
            }
        }
    }
}