package com.mospolytech.features.account.applications

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mospolytech.domain.account.model.Application
import com.mospolytech.features.base.utils.ClickListener
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
    Column(Modifier.fillMaxWidth()) {
        Text(text = application?.question.orEmpty(),
            modifier = Modifier
                .widthIn(min = 200.dp)
                .placeholder(application.isNull()))
        Text(text = application?.number.orEmpty(),
            modifier = Modifier
                .widthIn(min = 200.dp)
                .placeholder(application.isNull()))
        Text(text = application?.department.orEmpty(),
            modifier = Modifier
                .widthIn(min = 200.dp)
                .placeholder(application.isNull()))
        Text(text = application?.status.orEmpty(),
            modifier = Modifier
                .widthIn(min = 200.dp)
                .placeholder(application.isNull()))
        Text(text = application?.creationDateTime.toString(),
            modifier = Modifier
                .widthIn(min = 200.dp)
                .placeholder(application.isNull()))
        Text(text = application?.statusDateTime.toString(),
            modifier = Modifier
                .widthIn(min = 200.dp)
                .placeholder(application.isNull()))
    }

}
