package com.mospolytech.mospolyhelper.data.account.deadlines.remote

import com.beust.klaxon.Klaxon
import com.mospolytech.mospolyhelper.data.account.applications.api.ApplicationsHerokuClient
import com.mospolytech.mospolyhelper.data.account.deadlines.api.DeadlinesHerokuClient
import com.mospolytech.mospolyhelper.domain.account.applications.model.Application
import com.mospolytech.mospolyhelper.domain.account.deadlines.model.Deadline
import com.mospolytech.mospolyhelper.domain.account.deadlines.model.MyPortfolio
import com.mospolytech.mospolyhelper.utils.*

class DeadlinesRemoteDataSource(
    private val client: DeadlinesHerokuClient
) {
    suspend fun get(sessionId: String): Result<List<Deadline>> {
        return try {
            val res = client.getDeadlines(sessionId)
            val deadlines = Klaxon()
                .parseArray<Deadline>(Klaxon().parse<MyPortfolio>(res)!!.otherInformation)!!
            deadlines.sortedBy {
                it.pinned
            }
            deadlines.sortedBy {
                !it.completed
            }
            Result.success(deadlines)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun set(sessionId: String, deadlines: List<Deadline>): Result<List<Deadline>> {
        return try {
            val res = client.setDeadlines(sessionId, MyPortfolio(Klaxon().toJsonString(deadlines)))
            Result.success(Klaxon()
                .parseArray(Klaxon().parse<MyPortfolio>(res)!!.otherInformation)!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}