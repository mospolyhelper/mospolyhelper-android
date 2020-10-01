package com.mospolytech.mospolyhelper.domain.schedule.repository

interface GroupListRepository {
    suspend fun getGroupList(): List<String>
}