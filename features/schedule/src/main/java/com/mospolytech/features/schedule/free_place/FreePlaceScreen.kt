package com.mospolytech.features.schedule.free_place

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.androidx.compose.getViewModel

@Composable
fun FreePlaceScreen(viewModel: FreePlaceViewModel = getViewModel()) {
    val state by viewModel.state.collectAsState()

    FreePlaceContent(state)
}

@Composable
fun FreePlaceContent(state: FreePlaceState) {

}