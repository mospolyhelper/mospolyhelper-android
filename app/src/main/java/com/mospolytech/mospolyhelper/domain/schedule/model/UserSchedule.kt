package com.mospolytech.mospolyhelper.domain.schedule.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class UserSchedule : Comparable<UserSchedule> {
    companion object {
        const val PREFIX_STUDENT = "st_"
        const val PREFIX_TEACHER = "te_"
        const val PREFIX_AUDITORIUM = "au_"
        const val PREFIX_ADVANCED_SEARCH = "av_"
    }
    abstract val title: String
    abstract val idGlobal: String

    override fun compareTo(other: UserSchedule): Int {
        return title.compareTo(other.title)
    }
}

@Serializable
@SerialName("student")
data class StudentSchedule(
    val id: String,
    override val title: String
) : UserSchedule() {
    override val idGlobal: String
        get() = PREFIX_STUDENT + id
}

@Serializable
@SerialName("teacher")
data class TeacherSchedule(
    val id: String,
    override val title: String
) : UserSchedule() {
    override val idGlobal: String
        get() = PREFIX_TEACHER + id
}

@Serializable
@SerialName("auditorium")
data class AuditoriumSchedule(
    val id: String,
    override val title: String
) : UserSchedule() {
    override val idGlobal: String
        get() = PREFIX_AUDITORIUM + id
}

@Serializable
@SerialName("advancedSearch")
data class AdvancedSearchSchedule(
    val filters: ScheduleFilters
) : UserSchedule()  {
    override val title: String = "Продвинутый поиск"
    override val idGlobal: String
        get() = UserSchedule.PREFIX_ADVANCED_SEARCH
}