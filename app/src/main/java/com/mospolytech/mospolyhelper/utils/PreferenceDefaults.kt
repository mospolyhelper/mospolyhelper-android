package com.mospolytech.mospolyhelper.utils

class PreferenceDefaults {
    companion object {
        const val ScheduleUser = ""
        const val ScheduleShowEmptyLessons = false

        const val ShowEndedLessons = false
        const val ShowCurrentLessons = true
        const val ShowNotStartedLessons = false
        val FilterTypes = emptySet<String>()
        const val ShowImportantLessons = false
        const val ShowAverageLessons = false
        const val ShowNotImportantLessons = false
        const val ShowNotLabeledLessons = false

        // Account
        const val Login = ""
        const val Password = ""
        const val SessionId = ""
        const val SaveLogin = false
        const val SavePassword = false
    }
}