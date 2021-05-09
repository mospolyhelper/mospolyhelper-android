package com.mospolytech.mospolyhelper.features.ui.schedule.model

class SchedulePack(
    val schedule: List<List<ScheduleItemPacked>>
) {
    class Builder {
        private var showEmptyPairs = false
        private var showLessonWindows = false

        fun withEmptyPairs(showEmptyPairs: Boolean) {
            this.showEmptyPairs = showEmptyPairs
        }

        fun withLessonWindows(showLessonWindows: Boolean) {
            this.showLessonWindows = showLessonWindows
        }

//        fun build(): SchedulePack {
//
//        }
    }
}