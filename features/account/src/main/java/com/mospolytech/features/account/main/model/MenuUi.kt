package com.mospolytech.features.account.main.model

enum class MenuUi {
    Auth, Personal, Students, Teachers, Classmates, Payments, Applications, Marks
}

fun MenuUi.print(): String = when (this) {
    MenuUi.Auth -> "Авторизация"
    MenuUi.Personal -> "Информация"
    MenuUi.Students -> "Студенты"
    MenuUi.Teachers -> "Преподаватели"
    MenuUi.Classmates -> "Одногруппники"
    MenuUi.Payments -> "Оплата"
    MenuUi.Applications -> "Справки"
    MenuUi.Marks -> "Успеваемость"
}