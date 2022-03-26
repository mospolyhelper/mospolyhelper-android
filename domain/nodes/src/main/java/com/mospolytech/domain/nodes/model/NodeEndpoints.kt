package com.mospolytech.domain.nodes.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class NodeEndpoints {
    @SerialName("/schedule/sources/{type}")
    ScheduleSourcesType,
    @SerialName("/schedule/sources")
    ScheduleSources,
    @SerialName("/schedules/{type}/{key}")
    SchedulesTypeKey,
    @SerialName("/lessons/review/{type}/{key}")
    LessonsReviewTypeKey,
    @SerialName("/schedule/free-place")
    ScheduleFreePlace
}