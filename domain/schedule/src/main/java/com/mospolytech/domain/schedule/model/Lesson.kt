package com.mospolytech.domain.schedule.model

import kotlinx.serialization.Serializable

@Serializable
data class Lesson(
    val title: String,
    val type: String,
    val teachers: List<Teacher>,
    val groups: List<Group>,
    val places: List<Place>,
): Comparable<Lesson> {
    override fun compareTo(other: Lesson): Int {
        val comparing = title.compareTo(other.title)
        return if (comparing != 0) {
            comparing
        } else {
            type.compareTo(other.type)
        }
    }

}