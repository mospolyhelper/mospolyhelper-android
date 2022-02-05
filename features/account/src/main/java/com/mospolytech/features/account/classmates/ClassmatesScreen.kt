package com.mospolytech.features.account.classmates

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.mospolytech.domain.account.model.Student
import com.mospolytech.features.base.core.utils.ClickListener
import com.mospolytech.features.base.core.utils.Typed1Listener
import com.mospolytech.features.base.elements.ErrorView
import org.koin.androidx.compose.getViewModel

@Composable
fun ClassmatesScreen(viewModel: ClassmatesViewModel = getViewModel()) {
    val state by viewModel.state.collectAsState()

    ClassmatesContent(state,
    retryListener = {viewModel.loadClassmates()},
    backListener = {viewModel.exit()},
    inputListener = {viewModel.inputName(it)})
}

@Composable
fun ClassmatesContent(state: ClassmatesState,
                      retryListener: ClickListener,
                      backListener: ClickListener,
                      inputListener: Typed1Listener<String>
) {
    var name by rememberSaveable { mutableStateOf("") }
    Scaffold(topBar = {
        TopAppBar(title = { TextField(value = name,
            onValueChange = {
            inputListener.invoke(it)
            name = it }, label = { Text("ФИО")})},
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
                items(state.filteredData) {
                    Student(it)
                    Spacer(modifier = Modifier.padding(2.dp))
                }
            }
        }
    }
}

@Composable
fun Student(student: Student?) {
    Card(shape = MaterialTheme.shapes.medium, elevation = 2.dp, modifier = Modifier.fillMaxWidth()) {
        ConstraintLayout(modifier = Modifier.padding(5.dp)) {
            val (name, image) = createRefs()
            Text(text = student?.name.orEmpty(),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.constrainAs(name) {
                    start.linkTo(image.end)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
                    .padding(start = 10.dp))
            student?.avatar?.let {
                Image(
                    painter = rememberImagePainter(
                        data = it,
                        builder = {
                            transformations(CircleCropTransformation())
                        }
                    ),
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp)
                        .constrainAs(image) {
                            start.linkTo(parent.start)
                            top.linkTo(parent.top)
                        }
                )
            }

        }
    }
}
