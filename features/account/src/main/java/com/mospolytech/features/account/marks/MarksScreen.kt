package com.mospolytech.features.account.marks

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
fun MarksScreen(viewModel: MarksViewModel = getViewModel()) {
    val state by viewModel.state.collectAsState()

    MarksContent(state)
}

@Composable
fun MarksContent(state: MarksState) {
    LazyColumn(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        when {
            state.isLoading -> {
                item {
                    CircularProgressIndicator()
                }
            }
            state.isError -> {
                item {
                    Text("error")
                }
            }
            else -> {
                items(state.data) {
                    Text(it.toString())
                }
            }
        }
    }
}
