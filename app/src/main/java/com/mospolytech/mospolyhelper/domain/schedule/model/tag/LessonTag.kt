package com.mospolytech.mospolyhelper.domain.schedule.model.tag

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class LessonTag(
    val title: String,
    val color: Int,
    val lessons: List<LessonTagKey>
    ) : Parcelable, Comparable<LessonTag> {

    override fun compareTo(other: LessonTag) =
        title.compareTo(other.title)
}