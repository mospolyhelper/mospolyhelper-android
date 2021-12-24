package com.mospolytech.features.account.personal

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.getViewModel

@Composable
fun PersonalScreen(viewModel: PersonalViewModel = getViewModel()) {
    val state by viewModel.state.collectAsState()

    PersonalContent(state)
}

@Composable
fun PersonalContent(state: PersonalState) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        when {
            state.isPersonalLoading -> {
                CircularProgressIndicator()
            }
            state.isPersonalError -> {
                Text("error")
            }
            else -> {
                state.personal?.let {
                    Text(state.personal.toString())
                }
            }
        }
        when {
            state.isOrdersLoading -> {
                CircularProgressIndicator()
            }
            state.isOrdersError -> {
                Text("error")
            }
            else -> {
                LazyColumn() {
                    items(state.orders) {
                        Text(it.toString())
                    }
                }
            }
        }
    }
}
