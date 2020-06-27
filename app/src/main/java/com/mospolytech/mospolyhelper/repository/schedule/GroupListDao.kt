package com.mospolytech.mospolyhelper.repository.schedule

import com.mospolytech.mospolyhelper.utils.ContextProvider

class GroupListDao(
    private val converter: ScheduleConverter = ScheduleConverter(),
    private val client: GroupListClient = GroupListClient(),
    private val groupListParser: GroupListJsonParser = GroupListJsonParser()
) {
    companion object {
        private const val TAG = "GroupListDao"
    }


    suspend fun download(): List<String> {
        val groupListString = client.getGroupList()
        return groupListParser.parseGroupList(groupListString).sorted()
    }

    fun read(): List<String> {
        val file = ContextProvider.getFilesDir()
            .resolve(ScheduleDao.GROUP_LIST_FOLDER)
            .resolve(ScheduleDao.GROUP_LIST_FILE)
        return converter.deserializeGroupList(file.readText())
    }

    fun save(groupList: List<String>) {
        val file = ContextProvider.getFilesDir()
            .resolve(ScheduleDao.GROUP_LIST_FOLDER)
            .resolve(ScheduleDao.GROUP_LIST_FILE)
        file.delete()
        file.parentFile?.mkdirs()
        file.createNewFile()
        file.writeText(converter.serializeGroupList(groupList))
    }
}