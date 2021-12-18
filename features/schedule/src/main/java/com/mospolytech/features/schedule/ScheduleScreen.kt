package com.mospolytech.features.schedule

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.androidx.compose.getViewModel

@Composable
fun ScheduleScreen(viewModel: ScheduleViewModel = getViewModel()) {
    val sources by viewModel.scheduleSources.collectAsState()
    LazyColumn {
        items(sources) { source ->
            Text(text = source.toString())
        }
    }
}