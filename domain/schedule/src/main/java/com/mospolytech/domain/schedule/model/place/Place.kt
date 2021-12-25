package com.mospolytech.domain.schedule.model.place

import kotlinx.serialization.Serializable

@Serializable
data class Place(
    val id: String,
    val title: String
) : Comparable<Place> {
    override fun compareTo(other: Place): Int {
        return title.compareTo(other.title)
    }
}