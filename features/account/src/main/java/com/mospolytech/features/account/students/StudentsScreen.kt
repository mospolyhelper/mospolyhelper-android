package com.mospolytech.features.account.students

import androidx.compose.foundation.Image
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.mospolytech.domain.account.model.Student
import com.mospolytech.domain.account.model.print
import com.mospolytech.features.account.classmates.ClassmatesState
import com.mospolytech.features.base.utils.ClickListener
import com.mospolytech.features.base.utils.TypedListener
import com.mospolytech.features.base.utils.isNull
import com.mospolytech.features.base.view.ErrorView
import com.mospolytech.features.base.view.placeholder
import org.koin.androidx.compose.getViewModel

@Composable
fun StudentsScreen(viewModel: StudentsViewModel = getViewModel()) {
    val state by viewModel.state.collectAsState()

    StudentsContent(state,
        retryListener = {viewModel.loadStudents()},
        backListener = {viewModel.navigateBack()},
        inputListener = {viewModel.inputName(it)})
}

@Composable
fun StudentsContent(state: StudentsState,
                      retryListener: ClickListener,
                      backListener: ClickListener,
                      inputListener: TypedListener<String>
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
                items(state.data) {
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
            val (name, image, type, group) = createRefs()
            Text(text = student?.name.orEmpty(),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.constrainAs(name) {
                    start.linkTo(image.end)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
                    .padding(start = 10.dp))
            Text(text = student?.educationType?.print().orEmpty(),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.constrainAs(type) {
                    start.linkTo(image.end)
                    end.linkTo(parent.end)
                    top.linkTo(name.bottom)
                    width = Dimension.fillToConstraints
                }
                    .padding(start = 10.dp))
            Text(text = student?.group.orEmpty(),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.constrainAs(group) {
                    start.linkTo(image.end)
                    end.linkTo(parent.end)
                    top.linkTo(type.bottom)
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
