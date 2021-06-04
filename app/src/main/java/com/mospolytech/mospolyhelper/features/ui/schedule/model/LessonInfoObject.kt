package com.mospolytech.mospolyhelper.features.ui.schedule.model

interface LessonInfoObject {
    val title: String
    val description: String
    val avatar: Int
    val onClickListener: () -> Unit
}