package com.mospolytech.features.schedule.lesson_info

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.mospolytech.domain.schedule.model.lesson.LessonInfo
import com.mospolytech.features.base.core.utils.ClickListener
import com.mospolytech.features.base.elements.PrimaryTopAppBar
import com.mospolytech.features.schedule.R
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
    Column(Modifier.fillMaxSize()) {
        PrimaryTopAppBar(
            title = stringResource(R.string.sch_schedule),
            onBackClick = onBackClick
        )

        Text(text = state.lessonInfo.toString())
    }
}