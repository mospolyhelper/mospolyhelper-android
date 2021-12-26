package com.mospolytech.features.account.personal

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
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
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.mospolytech.domain.account.model.Order
import com.mospolytech.domain.account.model.Personal
import com.mospolytech.features.base.utils.ClickListener
import com.mospolytech.features.base.utils.format
import org.koin.androidx.compose.getViewModel

@Composable
fun PersonalScreen(viewModel: PersonalViewModel = getViewModel()) {
    val state by viewModel.state.collectAsState()

    PersonalContent(state,
    backListener = {viewModel.navigateBack()})
}

@Composable
fun PersonalContent(state: PersonalState, backListener: ClickListener) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Scaffold(topBar = {
            MediumTopAppBar(title = {Text("Информация", fontSize = 22.sp)},
                navigationIcon = { IconButton(onClick = { backListener.invoke() }) { Icon(Icons.Filled.ArrowBack, contentDescription = "Назад") } })
        }) {
           Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
               when {
                   state.isPersonalLoading -> {
                       CircularProgressIndicator()
                   }
                   state.isPersonalError -> {
                       Text("error")
                   }
                   else -> {
                       state.personal?.let {
                           Personal(personal = it)
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
                       LazyColumn(Modifier.fillMaxWidth()) {
                           items(state.orders) {
                               Order(order = it)
                               Spacer(modifier = Modifier.padding(2.dp))
                           }
                       }
                   }
               }
           }
        }

    }
}

@Composable
fun Personal(personal: Personal) {
    Text(personal.toString())
}

@Composable
fun Order(order: Order) {
    Card(shape = MaterialTheme.shapes.medium, elevation = 2.dp, modifier = Modifier.fillMaxWidth()) {
        ConstraintLayout(Modifier.padding(5.dp)) {
            val (number, date, info, icon) = createRefs()
            Text(text = "Приказ №${order.number}",
                modifier = Modifier.constrainAs(number) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    end.linkTo(date.end)
                    width = Dimension.fillToConstraints
                })
            Text(text = order.date.format(),
                modifier = Modifier.constrainAs(date) {
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                })
            order.additionalInfo?.let {
                Icon(imageVector = Icons.Filled.Info, contentDescription = "Информация",
                modifier = Modifier.constrainAs(icon) {
                    start.linkTo(parent.start)
                    linkTo(top = number.bottom, bottom = parent.bottom, bias = 0f)
                })
                Text(text = order.additionalInfo.orEmpty(),
                    modifier = Modifier.constrainAs(info) {
                        start.linkTo(icon.end)
                        bottom.linkTo(icon.bottom)
                        top.linkTo(icon.top)
                    })
            }


        }
    }
}
