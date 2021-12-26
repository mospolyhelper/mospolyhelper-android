package com.mospolytech.features.account.applications

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.mospolytech.domain.account.model.Application
import com.mospolytech.features.base.utils.ClickListener
import com.mospolytech.features.base.utils.format
import com.mospolytech.features.base.utils.isNull
import com.mospolytech.features.base.view.ErrorView
import com.mospolytech.features.base.view.placeholder
import org.koin.androidx.compose.getViewModel

@Composable
fun ApplicationsScreen(viewModel: ApplicationsViewModel = getViewModel()) {
    val state by viewModel.state.collectAsState()

    ApplicationsContent(state,
    retryListener = { viewModel.loadData() },
    backListener = { viewModel.navigateBack() })
}

@Composable
fun ApplicationsContent(state: ApplicationsState, retryListener: ClickListener, backListener: ClickListener) {
    Scaffold(topBar = {
        MediumTopAppBar(title = {Text("Справки", fontSize = 22.sp)},
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
                    Application(it)
                    Spacer(modifier = Modifier.padding(2.dp))
                }
            }
        }
    }
}

@Composable
fun Application(application: Application?) {
    Card(shape = MaterialTheme.shapes.medium, elevation = 2.dp, modifier = Modifier.fillMaxWidth()) {
        ConstraintLayout(modifier = Modifier.padding(5.dp)) {
            val (name, number, status, info, date) = createRefs()
            IconText(text = application?.question, modifier = Modifier.constrainAs(name) {
                start.linkTo(parent.start)
                top.linkTo(parent.top)
            })
            IconText(text = application?.number, modifier = Modifier.constrainAs(number) {
                start.linkTo(parent.start)
                top.linkTo(name.bottom)
            })
            IconText(text = application?.status, modifier = Modifier.constrainAs(status) {
                end.linkTo(parent.end)
                top.linkTo(parent.top)
            })
            IconText(text = application?.additionalInfo, modifier = Modifier.constrainAs(info) {
                start.linkTo(parent.start)
                top.linkTo(number.bottom)
            })
//            IconText(text = application?.creationDateTime?.format(), modifier = Modifier.constrainAs(date) {
//                start.linkTo(parent.start)
//                top.linkTo(name.bottom)
//            })
        }
    }
}

@Composable
fun IconText(text: String?, icon: ImageVector? = null, modifier: Modifier = Modifier) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier.padding(2.dp)) {
        text?.let {
            icon?.let {
                Icon(icon, "", modifier = Modifier.padding(end = 8.dp))
            }
            Text(it)
        }
    }

}
