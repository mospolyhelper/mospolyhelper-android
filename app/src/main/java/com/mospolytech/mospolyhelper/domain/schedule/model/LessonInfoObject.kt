package com.mospolytech.mospolyhelper.domain.schedule.model

interface LessonInfoObject {
    val title: String
    val description: String
    val avatar: Int
    val onClickListener: () -> Unit
}