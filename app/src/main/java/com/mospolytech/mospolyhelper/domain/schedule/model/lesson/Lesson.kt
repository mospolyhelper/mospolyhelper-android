package com.mospolytech.mospolyhelper.domain.schedule.model.lesson

import android.os.Parcelable
import com.mospolytech.mospolyhelper.domain.schedule.model.auditorium.Auditorium
import com.mospolytech.mospolyhelper.domain.schedule.model.group.Group
import com.mospolytech.mospolyhelper.domain.schedule.model.teacher.Teacher
import com.mospolytech.mospolyhelper.domain.schedule.utils.LessonTypeUtils
import com.mospolytech.mospolyhelper.utils.LocalDateSerializer
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Parcelize
@Serializable
data class Lesson(
    val title: String,
    val type: String,
    val teachers: List<Teacher>,
    val auditoriums: List<Auditorium>,
    val groups: List<Group>,
    @Serializable(with = LocalDateSerializer::class)
    val dateFrom: LocalDate,
    @Serializable(with = LocalDateSerializer::class)
    val dateTo: LocalDate,
) : Comparable<Lesson>, Parcelable {
    companion object {
        val Empty = Lesson(
            "",
            "",
            emptyList(),
            emptyList(),
            listOf(),
            LocalDate.MIN,
            LocalDate.MAX
        )
    }

    val isEmpty
        get() = title.isEmpty() && type.isEmpty()

    val isNotEmpty
        get() = title.isNotEmpty() || type.isNotEmpty()

    val isImportant
        get() = LessonTypeUtils.isTypeImportant(type)


    override fun compareTo(other: Lesson): Int {
        val g1 = groups.joinToString()
        val g2 = other.groups.joinToString()
        val groupComparing = g1.compareTo(g2)
        if (groupComparing != 0) return groupComparing
        if (dateFrom != other.dateFrom) return dateFrom.compareTo(other.dateFrom)
        return dateTo.compareTo(other.dateTo)
    }
}