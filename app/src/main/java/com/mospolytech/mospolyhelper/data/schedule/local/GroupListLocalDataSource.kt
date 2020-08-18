package com.mospolytech.mospolyhelper.data.schedule.local

import com.mospolytech.mospolyhelper.App
import com.mospolytech.mospolyhelper.data.schedule.converter.ScheduleLocalConverter

class GroupListLocalDataSource(
    private val localConverter: ScheduleLocalConverter = ScheduleLocalConverter()
) {
    companion object {
        const val GROUP_LIST_FILE = "group_list"
        const val GROUP_LIST_FOLDER = "cached_group_list"
    }

    fun get(): List<String> {
        val file =  App.context!!.filesDir
            .resolve(GROUP_LIST_FOLDER)
            .resolve(GROUP_LIST_FILE)
        return localConverter.deserializeGroupList(file.readText())
    }

    fun set(groupList: List<String>) {
        val file =  App.context!!.filesDir
            .resolve(GROUP_LIST_FOLDER)
            .resolve(GROUP_LIST_FILE)
        file.delete()
        file.parentFile?.mkdirs()
        file.createNewFile()
        file.writeText(localConverter.serializeGroupList(groupList))
    }
}