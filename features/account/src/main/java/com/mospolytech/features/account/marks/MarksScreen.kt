package com.mospolytech.features.account.marks

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mospolytech.domain.account.model.Mark
import com.mospolytech.features.base.utils.ClickListener
import com.mospolytech.features.base.utils.TypedListener
import com.mospolytech.features.base.view.ErrorView
import org.koin.androidx.compose.getViewModel

@Composable
fun MarksScreen(viewModel: MarksViewModel = getViewModel()) {
    val state by viewModel.state.collectAsState()

    MarksContent(state,
        retryListener = { viewModel.loadMarks() },
        backListener = { viewModel.navigateBack() },
        inputListener = {  }
    )
}

@Composable
fun MarksContent(state: MarksState,
                 retryListener: ClickListener,
                 backListener: ClickListener,
                 inputListener: TypedListener<Int>) {
    Scaffold(topBar = {
        var expandedState by remember { mutableStateOf(false) }
        var selectedIndex by remember { mutableStateOf(if (state.coursesAndSemesters.keys.isNotEmpty()) state.coursesAndSemesters.keys.first() else 1) }
        MediumTopAppBar(title = { DropdownMenu(expanded = expandedState, onDismissRequest = { expandedState = false }) {
            Text("${state.coursesAndSemesters[selectedIndex]} курс $selectedIndex семестр",
                modifier = Modifier.fillMaxWidth().clickable(onClick = { expandedState = true }))
            state.coursesAndSemesters.forEach {
                DropdownMenuItem(onClick = {
                    selectedIndex = it.key
                    expandedState = false
                }) {
                    Text(text = "${it.value} курс ${it.key} семестр")
                }
            }
        }},
            navigationIcon = { IconButton(onClick = { backListener.invoke() }) { Icon(Icons.Filled.ArrowBack, contentDescription = "Назад") } })
    }) {
        LazyColumn(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            if (state.isError && state.data.isEmpty()) {
                item {
                    ErrorView {
                        retryListener.invoke()
                    }
                }
            } else {
                items(state.data.map { it.marks }.flatten()) {
                    Mark(it)
                    Spacer(modifier = Modifier.padding(2.dp))
                }
            }
        }
    }
}

@Composable
fun Mark(mark: Mark) {
    Text(mark.toString())
}
