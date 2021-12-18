package com.mospolytech.domain.schedule.model

import kotlinx.serialization.Serializable

@Serializable
data class Place(
    val title: String
) : Comparable<Place> {
    override fun compareTo(other: Place): Int {
        return title.compareTo(other.title)
    }
}