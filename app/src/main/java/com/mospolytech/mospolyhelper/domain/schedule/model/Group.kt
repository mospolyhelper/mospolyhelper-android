package com.mospolytech.mospolyhelper.domain.schedule.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class Group(
    val title: String,
    val isEvening: Boolean
) : Parcelable {
    companion object {
        val empty =
            Group(
                "",
                false
            )

        fun fromTitle(title: String): Group {
            return Group(
                title,
                false
            )
        }

        fun getShort(groups: List<Group>): String {
            var resGroup = ""
            var flag = true

            if (groups.size > 1) {
                val partsOfFirstGroup = groups.first().title.split("-")
                if (partsOfFirstGroup.size == 2) {
                    val firstPart = partsOfFirstGroup.first()
                    if (groups.all { it.title.contains(firstPart, true) }) {
                        resGroup = groups.first().title + ", " + groups.subList(1, groups.size)
                            .joinToString { it.title.takeLast(it.title.length - firstPart.length) }
                        flag = false
                    }
                }
            }

            if (flag) {
                resGroup = groups.joinToString { it.title }
            }

            return resGroup
        }
    }
}