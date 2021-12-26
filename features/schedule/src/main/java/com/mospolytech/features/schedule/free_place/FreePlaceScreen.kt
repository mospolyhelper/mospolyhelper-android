package com.mospolytech.features.schedule.free_place

import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.RangeSlider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.getViewModel

@Composable
fun FreePlaceScreen(viewModel: FreePlaceViewModel = getViewModel()) {
    val state by viewModel.state.collectAsState()

    FreePlaceContent(
        state,
        viewModel::onDateRangeChange,
        viewModel::onTimeRangeChange
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FreePlaceContent(
    state: FreePlaceState,
    onDateRangeChange: (ClosedFloatingPointRange<Float>) -> Unit,
    onTimeRangeChange: (ClosedFloatingPointRange<Float>) -> Unit
) {
    Column(Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        Spacer(Modifier.height(10.dp))
        Text(text = state.dateFrom.toString())
        Text(text = state.dateTo.toString())
        RangeSlider(values = state.datesPositionRange, onValueChange = onDateRangeChange)
        Spacer(Modifier.height(10.dp))
        Text(text = state.timeFrom.toString())
        Text(text = state.timeTo.toString())
        RangeSlider(values = state.timesPositionRange, onValueChange = onTimeRangeChange)
    }
}