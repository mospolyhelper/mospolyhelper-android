package com.mospolytech.mospolyhelper.data.account.deadlines.remote

import kotlinx.serialization.*
import kotlinx.serialization.json.*
import com.mospolytech.mospolyhelper.data.account.deadlines.api.DeadlinesHerokuClient
import com.mospolytech.mospolyhelper.domain.account.deadlines.model.Deadline
import com.mospolytech.mospolyhelper.domain.account.deadlines.model.MyPortfolio
import com.mospolytech.mospolyhelper.utils.*

class DeadlinesRemoteDataSource(
    private val client: DeadlinesHerokuClient
) {
    suspend fun get(sessionId: String): Result<List<Deadline>> {
        return try {
            val res = client.getDeadlines(sessionId)
            var deadlines = Json.decodeFromString<List<Deadline>>(Json.decodeFromString<MyPortfolio>(res).otherInformation)
            deadlines = deadlines.sortedBy {
                it.pinned
            }
            deadlines = deadlines.sortedBy {
                !it.completed
            }
            Result.success(deadlines)
        } catch (e: SerializationException) {
            Result.success(emptyList())
        } catch (e: Exception) {
            Result.failure(e)
        }

    }

    suspend fun set(sessionId: String, deadlines: List<Deadline>): Result<List<Deadline>> {
        return try {
            var list = deadlines.sortedBy {
                it.pinned
            }
            list = list.sortedBy {
                !it.completed
            }
            val res = client.setDeadlines(sessionId, MyPortfolio(Json.encodeToString(list)))
            Result.success(Json.decodeFromString<List<Deadline>>(Json.decodeFromString<MyPortfolio>(res).otherInformation))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}