package com.mospolytech.mospolyhelper.domain.schedule.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class UserSchedule {
    abstract val id: String
    abstract val title: String
}

@Serializable
@SerialName("student")
class StudentSchedule(
    override val id: String,
    override val title: String,
) : UserSchedule()

@Serializable
@SerialName("teacher")
class TeacherSchedule(
    override val id: String,
    override val title: String,
) : UserSchedule()

@Serializable
@SerialName("auditorium")
class AuditoriumSchedule(
    override val id: String,
    override val title: String,
) : UserSchedule()