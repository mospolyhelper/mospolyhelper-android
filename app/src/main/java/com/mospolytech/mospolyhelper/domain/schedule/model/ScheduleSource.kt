package com.mospolytech.mospolyhelper.domain.schedule.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class ScheduleSource : Comparable<ScheduleSource> {
    companion object {
        const val PREFIX_STUDENT = "st_"
        const val PREFIX_TEACHER = "te_"
        const val PREFIX_AUDITORIUM = "au_"
        const val PREFIX_ADVANCED_SEARCH = "av_"
    }
    abstract val title: String
    abstract val idGlobal: String

    override fun compareTo(other: ScheduleSource): Int {
        return title.compareTo(other.title)
    }
}

@Serializable
@SerialName("student")
data class StudentScheduleSource(
    val id: String,
    override val title: String
) : ScheduleSource() {
    override val idGlobal: String
        get() = PREFIX_STUDENT + id
}

@Serializable
@SerialName("teacher")
data class TeacherScheduleSource(
    val id: String,
    override val title: String
) : ScheduleSource() {
    override val idGlobal: String
        get() = PREFIX_TEACHER + id
}

@Serializable
@SerialName("auditorium")
data class AuditoriumScheduleSource(
    val id: String,
    override val title: String
) : ScheduleSource() {
    override val idGlobal: String
        get() = PREFIX_AUDITORIUM + id
}

@Serializable
@SerialName("advancedSearch")
data class AdvancedSearchScheduleSource(
    val filters: ScheduleFilters
) : ScheduleSource()  {
    override val title: String = "Продвинутый поиск"
    override val idGlobal: String
        get() = ScheduleSource.PREFIX_ADVANCED_SEARCH
}