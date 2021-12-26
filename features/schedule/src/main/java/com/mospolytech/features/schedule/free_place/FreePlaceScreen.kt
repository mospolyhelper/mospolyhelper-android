package com.mospolytech.features.schedule.free_place

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.TextField
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mospolytech.features.base.utils.ContentAlpha
import com.mospolytech.features.base.utils.MaterialTheme3
import com.mospolytech.features.base.utils.WithContentAlpha
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import org.koin.androidx.compose.getViewModel
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun FreePlaceScreen(viewModel: FreePlaceViewModel = getViewModel()) {
    val state by viewModel.state.collectAsState()

    FreePlaceContent(
        state,
        viewModel::onDateSelect,
        viewModel::onTimeFromSelect,
        viewModel::onTimeToSelect,
        viewModel::onEnterFilterQuery,
        viewModel::onFindFreePlaces
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FreePlaceContent(
    state: FreePlaceState,
    onDateSelect: (LocalDate) -> Unit,
    onTimeFromSelect: (LocalTime) -> Unit,
    onTimeToSelect: (LocalTime) -> Unit,
    onEnterFilterQuery: (String) -> Unit,
    onFindFreePlaces: () -> Unit,
) {
    Box(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        val dialogDatePickerState = rememberMaterialDialogState()
        val dialogTimePickerFromState = rememberMaterialDialogState()
        val dialogTimePickerToState = rememberMaterialDialogState()

//        Spacer(Modifier.height(10.dp))
//        Text(text = state.dateFrom.toString())
//        Text(text = state.dateTo.toString())
//        RangeSlider(values = state.datesPositionRange, onValueChange = onDateRangeChange)
//        Spacer(Modifier.height(10.dp))
//        Text(text = state.timeFrom.toString())
//        Text(text = state.timeTo.toString())
//        RangeSlider(values = state.timesPositionRange, onValueChange = onTimeRangeChange)

        Column(Modifier.verticalScroll(rememberScrollState())) {
            Text(text = state.date.toString())
            Text(text = state.timeFrom.toString())
            Text(text = state.timeTo.toString())
            Button(onClick = { dialogDatePickerState.show() }) {
                Text(text = "Выбрать дату")
            }
            Button(onClick = { dialogTimePickerFromState.show() }) {
                Text(text = "Выбрать начальное время")
            }
            Button(onClick = { dialogTimePickerToState.show() }) {
                Text(text = "Выбрать конечное время")
            }
            TextField(value = state.filterQuery, onValueChange = onEnterFilterQuery)
            
            LazyColumn(
                Modifier
                    .fillMaxWidth()
                    .height(300.dp)) {
                items(state.filteredPlaces) { item ->
                    var checked by remember { mutableStateOf(false) }
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable { checked = !checked }) {
                        Text(
                            text = item.title,
                            modifier = Modifier
                                .weight(weight = 1f, fill = true)
                                .padding(horizontal = 10.dp, vertical = 10.dp)

                        )

                        Checkbox(
                            checked = checked,
                            onCheckedChange = { checked = it }
                        )
                    }
                }
            }
            Button(onClick = onFindFreePlaces) {
                Text(text = "Найти свободные аудитории")
            }
            val freePlaces = remember(state.freePlaces) {
                state.freePlaces.toList()
            }
            LazyColumn(
                Modifier
                    .fillMaxWidth()
                    .height(300.dp)) {
                items(freePlaces) { item ->
                    var checked by remember { mutableStateOf(false) }
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .clickable { checked = !checked }) {
                        Text(
                            text = item.first.title,
                            style = MaterialTheme3.typography.titleSmall
                        )
                        WithContentAlpha(alpha = ContentAlpha.medium) {
                            Text(
                                text = "Занятий в это время: " + item.second.size.toString(),
                                style = MaterialTheme3.typography.bodySmall
                            )
                        }
                        Spacer(Modifier.height(10.dp))
                    }
                }
            }
        }



        MaterialDialog(
            dialogState = dialogDatePickerState,
            buttons = {
                positiveButton("Ок")
                negativeButton("Отмена")
            }
        ) {
            datepicker(title = "Выберите день") { date ->
                onDateSelect(date)
            }
        }

        MaterialDialog(
            dialogState = dialogTimePickerFromState,
            buttons = {
                positiveButton("Ок")
                negativeButton("Отмена")
            }
        ) {
            timepicker(title = "Выберите начальное время", is24HourClock = true) { time ->
                onTimeFromSelect(time)
            }
        }

        MaterialDialog(
            dialogState = dialogTimePickerToState,
            buttons = {
                positiveButton("Ок")
                negativeButton("Отмена")
            }
        ) {
            timepicker(title = "Выбрать конечное время", is24HourClock = true) { time ->
                onTimeToSelect(time)
            }
        }
    }
}