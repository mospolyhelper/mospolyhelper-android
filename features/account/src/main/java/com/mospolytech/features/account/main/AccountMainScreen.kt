package com.mospolytech.features.account.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mospolytech.features.account.main.model.MenuUi
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AccountMainScreen(viewModel: AccountMainViewModel = getViewModel()) {
    val state by viewModel.state.collectAsState()

    AccountContent(state) { it.action(viewModel) }
}

@ExperimentalMaterialApi
@Composable
fun AccountContent(state: AccountMenuState, onClickListener: (MenuUi) -> Unit) {
    Column(Modifier.padding(top = 10.dp).fillMaxSize()) {
        Row(
            Modifier
                .height(100.dp)
                .fillMaxWidth()
        ) {
            Card(
                modifier = Modifier
                    .padding(horizontal = 5.dp, vertical = 5.dp)
                    .fillMaxHeight()
                    .fillMaxWidth(0.6f),
                shape = RoundedCornerShape(16.dp),
                onClick = {onClickListener.invoke(MenuUi.Auth)}
            ) {
                Box(Modifier.padding(horizontal = 15.dp, vertical = 10.dp)) {
                    Text(text = "Авторизация")
                }
            }
        }
        Row(
            Modifier
                .height(100.dp)
                .fillMaxWidth()
        ) {
            Card(
                modifier = Modifier
                    .padding(horizontal = 5.dp, vertical = 5.dp)
                    .fillMaxHeight()
                    .fillMaxWidth(0.6f),
                shape = RoundedCornerShape(16.dp),
                onClick = {onClickListener.invoke(MenuUi.Personal)}
            ) {
                Column(Modifier.padding(horizontal = 15.dp, vertical = 10.dp)) {
                    Text(text = "Информация")
                }
            }

            Card(
                modifier = Modifier
                    .padding(horizontal = 5.dp, vertical = 5.dp)
                    .fillMaxHeight()
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                onClick = {onClickListener.invoke(MenuUi.Students)}
            ) {
                Column(Modifier.padding(horizontal = 15.dp, vertical = 10.dp)) {
                    Text(text = "Студенты")
                }
            }
        }
        Row(
            Modifier
                .height(100.dp)
                .fillMaxWidth()
        ) {
            Card(
                modifier = Modifier
                    .padding(horizontal = 5.dp, vertical = 5.dp)
                    .fillMaxHeight()
                    .fillMaxWidth(0.5f),
                shape = RoundedCornerShape(16.dp),
                onClick = {onClickListener.invoke(MenuUi.Teachers)}
            ) {
                Box(Modifier.padding(horizontal = 15.dp, vertical = 10.dp)) {
                    Text(text = "Преподаватели")
                }
            }
            Card(
                modifier = Modifier
                    .padding(horizontal = 5.dp, vertical = 5.dp)
                    .fillMaxHeight()
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                onClick = {onClickListener.invoke(MenuUi.Classmates)}
            ) {
                Box(Modifier.padding(horizontal = 15.dp, vertical = 10.dp)) {
                    Text(text = "Одногруппники")
                }
            }
        }
        Row(
            Modifier
                .height(100.dp)
                .fillMaxWidth()
        ) {
            Card(
                modifier = Modifier
                    .padding(horizontal = 5.dp, vertical = 5.dp)
                    .fillMaxHeight()
                    .fillMaxWidth(0.3f),
                shape = RoundedCornerShape(16.dp),
                onClick = {onClickListener.invoke(MenuUi.Payments)}
            ) {
                Box(Modifier.padding(horizontal = 15.dp, vertical = 10.dp)) {
                    Text(text = "Оплаты")
                }
            }
            Card(
                modifier = Modifier
                    .padding(horizontal = 5.dp, vertical = 5.dp)
                    .fillMaxHeight()
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                onClick = {onClickListener.invoke(MenuUi.Applications)}
            ) {
                Box(Modifier.padding(horizontal = 15.dp, vertical = 10.dp)) {
                    Text(text = "Справки, заявления")
                }
            }
        }
        Row(
            Modifier
                .height(100.dp)
                .fillMaxWidth()
        ) {
            Card(
                modifier = Modifier
                    .padding(horizontal = 5.dp, vertical = 5.dp)
                    .fillMaxHeight()
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                onClick = {onClickListener.invoke(MenuUi.Marks)}
            ) {
                Box(Modifier.padding(horizontal = 15.dp, vertical = 10.dp)) {
                    Text(text = "Оценки")
                }
            }
        }
    }
}

private fun MenuUi.action(viewModel: AccountMainViewModel) {
    when (this) {
        MenuUi.Auth -> viewModel.navigateToAuth()
        MenuUi.Personal -> viewModel.navigateToPersonal()
        MenuUi.Students -> viewModel.navigateToStudents()
        MenuUi.Teachers -> viewModel.navigateToTeachers()
        MenuUi.Classmates -> viewModel.navigateToClassmates()
        MenuUi.Payments -> viewModel.navigateToPayments()
        MenuUi.Applications -> viewModel.navigateToApplications()
        MenuUi.Marks -> viewModel.navigateToMarks()
    }
}