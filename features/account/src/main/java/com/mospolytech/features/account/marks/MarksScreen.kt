package com.mospolytech.features.account.marks

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.mospolytech.domain.account.model.Mark
import com.mospolytech.domain.account.model.print
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
        TopAppBar(title = {
                          Text(text = "Оценки")
//            DropdownMenu(expanded = expandedState, onDismissRequest = { expandedState = false }) {
//            Text("${state.coursesAndSemesters[selectedIndex]} курс $selectedIndex семестр",
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .clickable(onClick = { expandedState = true }))
//            state.coursesAndSemesters.forEach {
//                DropdownMenuItem(onClick = {
//                    selectedIndex = it.key
//                    expandedState = false
//                }) {
//                    Text(text = "${it.value} курс ${it.key} семестр")
//                }
//            }
//        }
                      },
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
    Card(shape = MaterialTheme.shapes.medium, elevation = 2.dp, modifier = Modifier.fillMaxWidth()) {
        ConstraintLayout(modifier = Modifier
            .padding(5.dp)
            .heightIn(70.dp)) {
            val (name, type, markText) = createRefs()
            Text(text = mark.name, modifier = Modifier.constrainAs(name) {
                start.linkTo(parent.start)
                top.linkTo(parent.top)
                end.linkTo(markText.start)
                width = Dimension.fillToConstraints
            })
            Text(text = mark.type.print(), modifier = Modifier.constrainAs(type) {
                linkTo(top = name.bottom, bottom = parent.bottom, bias = 1f)
                start.linkTo(parent.start)
                end.linkTo(markText.start)
                width = Dimension.fillToConstraints
            })
            Text(text = mark.mark,
                fontSize = 25.sp,
                modifier = Modifier.constrainAs(markText) {
                end.linkTo(parent.end)
                linkTo(top = parent.top, bottom = parent.bottom)
            })
        }
    }
}
