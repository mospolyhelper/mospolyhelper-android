package com.mospolytech.features.account.payments

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.mospolytech.domain.account.model.Payment
import com.mospolytech.domain.account.model.Payments
import com.mospolytech.domain.account.model.print
import com.mospolytech.features.base.core.utils.ClickListener
import com.mospolytech.features.base.core.utils.format
import com.mospolytech.features.base.elements.ErrorView
import org.koin.androidx.compose.getViewModel

@Composable
fun PaymentsScreen(viewModel: PaymentsViewModel = getViewModel()) {
    val state by viewModel.state.collectAsState()

    PaymentsContent(state,
        retryListener = { viewModel.load() },
        backListener = { viewModel.exit() },)
}

@Composable
fun PaymentsContent(state: PaymentsState,
                    retryListener: ClickListener,
                    backListener: ClickListener) {
    Scaffold(topBar = {
        TopAppBar(title = {Text("Платежи", fontSize = 22.sp)},
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
                item {
                    LazyRow() {
                        items(state.types) {
                            Button({}, modifier = Modifier.padding(2.dp)) {
                                Text(text = it.print())
                            }
                        }
                    }
                }
                state.data.firstOrNull()?.let {
                    item {
                        Payment(it)
                    }
                    items(it.payments) { payment ->
                        PaymentInfo(payment = payment)
                    }
                }

            }
        }
    }
}

@Composable
fun PaymentInfo(payment: Payment) {
    Card(shape = MaterialTheme.shapes.medium, elevation = 2.dp, modifier = Modifier
        .fillMaxWidth()
        .padding(2.dp)) {
        ConstraintLayout(modifier = Modifier
            .padding(5.dp)
            .heightIn(20.dp)) {
            val (date, sum) = createRefs()
            Text(payment.date.format(), modifier = Modifier.constrainAs(date) {
                start.linkTo(parent.start)
                top.linkTo(parent.top)
                end.linkTo(sum.start)
                width = Dimension.fillToConstraints
            })
            Text(payment.payment.toString(), modifier = Modifier.constrainAs(sum) {
                top.linkTo(parent.top)
                end.linkTo(parent.end)
            })
        }
    }
}

@Composable
fun Payment(payment: Payments) {
    Card(shape = MaterialTheme.shapes.medium, elevation = 2.dp, modifier = Modifier.fillMaxWidth()
        .padding(2.dp)) {
        Column(modifier = Modifier
            .padding(5.dp)
            .heightIn(40.dp)) {
            Text("Номер договора: ${payment.id}")
            Text("Дата договора: ${payment.date?.format()}")
            Text("Сумма договора: ${payment.sum}")
            Text("Задолженность: ${payment.credit}")
        }
    }
}
