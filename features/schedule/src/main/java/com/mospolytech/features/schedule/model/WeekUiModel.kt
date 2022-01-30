package com.mospolytech.features.schedule.model

import com.mospolytech.domain.base.utils.WeekIterator
import com.mospolytech.domain.base.utils.getCeilSunday
import com.mospolytech.domain.base.utils.getFloorMonday
import com.mospolytech.domain.schedule.model.schedule.ScheduleDay
import java.time.LocalDate

data class WeekUiModel(
    val days: List<DayUiModel>
) {
    companion object {
        fun fromSchedule(
            schedule: List<ScheduleDay>,
            today: LocalDate = LocalDate.now()
        ): List<WeekUiModel> {
            val dateFrom = schedule.firstOrNull()?.date?.getFloorMonday()
            val dateTo = schedule.lastOrNull()?.date?.getCeilSunday()

            if (dateFrom == null || dateTo == null) return emptyList()
            return WeekIterator(dateFrom, dateTo).map {
                WeekUiModel(
                    it.map { date ->
                        DayUiModel(
                            date,
                            date == today,
                            schedule.firstOrNull { it.date == date }?.lessons?.size ?: 0
                        )
                    }
                )
            }
        }
    }
}