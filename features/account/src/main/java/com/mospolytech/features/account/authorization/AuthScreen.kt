package com.mospolytech.features.account.authorization

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mospolytech.features.base.utils.ClickListener
import com.mospolytech.features.base.utils.Typed2ClickListener
import org.koin.androidx.compose.getViewModel

@Composable
fun AuthScreen(viewModel: AuthViewModel = getViewModel()) {
    val state by viewModel.state.collectAsState()

    AuthContent(state,
    onLoginClick = {login, password -> viewModel.authorize(login, password)},
    onLoggedClick = {viewModel.back()})
}

@Composable
fun AuthContent(state: AuthState, onLoginClick: Typed2ClickListener<String, String>, onLoggedClick: ClickListener) {
    Scaffold(topBar = {
        MediumTopAppBar(title = {Text("Авторизация", fontSize = 22.sp)},
        navigationIcon = {IconButton(onClick = { onLoggedClick.invoke() }) {Icon(Icons.Filled.ArrowBack, contentDescription = "Назад") }})
    }) {
        if (state.auth) {
            Authorized(state = state) { onLoggedClick.invoke() }
        } else {
            NotAuthorized(state = state) {login, password -> onLoginClick.invoke(login, password)}
        }
    }
}

@Composable
fun NotAuthorized(state: AuthState, onAuthorize: Typed2ClickListener<String,String>) {
    var login by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        TextField(
            value = login,
            onValueChange = {
                login = it
            },
            label = { Text("Логин") }
        )
        TextField(
            value = password,
            onValueChange = {
                password = it
            },
            label = { Text("Пароль") }
        )
        Button(onClick = { onAuthorize.invoke(login, password) }) {
            Text(text = "Войти")
        }
    }
}
@Composable
fun Authorized(state: AuthState, listener: ClickListener) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(text = "Привет ${state.name}")
        Button(onClick = { listener.invoke() }) {
            Text(text = "Перейти к меню")
        }
    }
}
