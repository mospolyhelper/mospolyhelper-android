package com.mospolytech.mospolyhelper.data.schedule.local

import com.mospolytech.mospolyhelper.App

class GroupListLocalDataSource {

    fun get(): List<String>? {
        val folder = App.context!!.filesDir
            .resolve(ScheduleLocalDataSource.SCHEDULE_FOLDER)
            .resolve(ScheduleLocalDataSource.SCHEDULE_STUDENT_FOLDER)

        if (!folder.exists() || !folder.isDirectory) {
            return null
        }
        return folder.listFiles()?.map { it.name }
    }
}