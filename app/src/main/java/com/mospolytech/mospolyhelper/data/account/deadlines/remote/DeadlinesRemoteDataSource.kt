package com.mospolytech.mospolyhelper.data.account.deadlines.remote

import com.mospolytech.mospolyhelper.data.account.deadlines.api.DeadlinesHerokuClient
import com.mospolytech.mospolyhelper.domain.account.deadlines.model.Deadline
import com.mospolytech.mospolyhelper.domain.account.deadlines.model.MyPortfolio
import com.mospolytech.mospolyhelper.utils.Result0
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class DeadlinesRemoteDataSource(
    private val client: DeadlinesHerokuClient
) {
    suspend fun get(sessionId: String): Result0<List<Deadline>> {
        return try {
            val res = client.getDeadlines(sessionId)
            var deadlines = Json.decodeFromString<List<Deadline>>(Json.decodeFromString<MyPortfolio>(res).otherInformation)
            deadlines = deadlines.sortedBy {
                it.pinned
            }
            deadlines = deadlines.sortedBy {
                !it.completed
            }
            Result0.Success(deadlines)
        } catch (e: SerializationException) {
            Result0.Success(emptyList())
        } catch (e: Exception) {
            Result0.Failure(e)
        }

    }

    suspend fun set(sessionId: String, deadlines: List<Deadline>): Result0<List<Deadline>> {
        return try {
            var list = deadlines.sortedBy {
                it.pinned
            }
            list = list.sortedBy {
                !it.completed
            }
            val res = client.setDeadlines(sessionId, MyPortfolio(Json.encodeToString(list)))
            Result0.Success(Json.decodeFromString<List<Deadline>>(Json.decodeFromString<MyPortfolio>(res).otherInformation))
        } catch (e: Exception) {
            Result0.Failure(e)
        }
    }
}