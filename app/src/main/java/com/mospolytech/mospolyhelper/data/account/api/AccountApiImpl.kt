package com.mospolytech.mospolyhelper.data.account.api

import com.mospolytech.mospolyhelper.data.core.local.SharedPreferencesDataSource
import com.mospolytech.mospolyhelper.data.utils.jsonToResult
import com.mospolytech.mospolyhelper.data.utils.toObject
import com.mospolytech.mospolyhelper.domain.account.model.applications.Application
import com.mospolytech.mospolyhelper.domain.account.model.auth.JwtModel
import com.mospolytech.mospolyhelper.domain.account.model.classmates.Classmate
import com.mospolytech.mospolyhelper.domain.account.model.deadlines.MyPortfolio
import com.mospolytech.mospolyhelper.domain.account.model.dialogs.DialogModel
import com.mospolytech.mospolyhelper.domain.account.model.statement.GradeSheet
import com.mospolytech.mospolyhelper.domain.account.model.statement.GradeSheetMark
import com.mospolytech.mospolyhelper.domain.account.model.info.Info
import com.mospolytech.mospolyhelper.domain.account.model.marks.Marks
import com.mospolytech.mospolyhelper.domain.account.model.dialog.Message
import com.mospolytech.mospolyhelper.domain.account.model.dialog.MessageSend
import com.mospolytech.mospolyhelper.domain.account.model.payments.Payments
import com.mospolytech.mospolyhelper.domain.account.model.statements.Statements
import com.mospolytech.mospolyhelper.domain.account.model.students.StudentsDto
import com.mospolytech.mospolyhelper.domain.account.model.teachers.TeachersDto
import com.mospolytech.mospolyhelper.utils.PreferenceDefaults
import com.mospolytech.mospolyhelper.utils.PreferenceKeys
import com.mospolytech.mospolyhelper.utils.Result0
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*

class AccountApiImpl(private val client: HttpClient,
                     prefDataSource: SharedPreferencesDataSource
): AccountApi {

    companion object {
        private const val BASE_URL = "https://mospolyhelper.herokuapp.com"
        private const val BASE_URL_V2 = "https://mospolyhelper.herokuapp.com/v0.2"

        private const val ACCOUNT_MODULE = "/account"

        private const val GET_APPLICATIONS = "$BASE_URL$ACCOUNT_MODULE/applications"
        private const val GET_AUTH = "$BASE_URL_V2$ACCOUNT_MODULE/authenticate"
        private const val GET_REFRESH = "$BASE_URL_V2$ACCOUNT_MODULE/refresh"
        private const val GET_CLASSMATES = "$BASE_URL$ACCOUNT_MODULE/classmates"
        private const val GET_PORTFOLIO = "$BASE_URL$ACCOUNT_MODULE/myportfolio"
        private const val GET_GRADE_SHEET = "$BASE_URL$ACCOUNT_MODULE/grade-sheet"
        private const val GET_GROUP_MARKS = "$BASE_URL$ACCOUNT_MODULE/grade-sheet-marks"
        private const val GET_INFO = "$BASE_URL$ACCOUNT_MODULE/info"
        private const val GET_MARKS = "$BASE_URL$ACCOUNT_MODULE/marks"
        private const val GET_DIALOG = "$BASE_URL$ACCOUNT_MODULE/dialog"
        private const val GET_DIALOGS = "$BASE_URL$ACCOUNT_MODULE/dialogs"
        private const val SEND_MESSAGE = "$BASE_URL$ACCOUNT_MODULE/message"
        private const val GET_PAYMENTS = "$BASE_URL$ACCOUNT_MODULE/payments"
        private const val GET_STATEMENTS = "$BASE_URL$ACCOUNT_MODULE/grade-sheets"
        private const val GET_PORTFOLIOS = "$BASE_URL$ACCOUNT_MODULE/portfolios"
        private const val GET_TEACHERS = "$BASE_URL$ACCOUNT_MODULE/teachers"

        private const val SESSION_ID_HEADER = "sessionId"
    }

    private val sessionId = prefDataSource.get(
        PreferenceKeys.SessionId,
        PreferenceDefaults.SessionId
    )

    override suspend fun getApplications(): Result0<List<Application>> {
        return executeResultObject {
            client.get(GET_APPLICATIONS) {
                header(SESSION_ID_HEADER, sessionId)
            }
        }
    }

    override suspend fun auth(login: String, password: String): Result0<JwtModel> {
        val params = mapOf(
            "login" to login,
            "password" to password
        )
        return executeResultObject {
            client.post(GET_AUTH) {
                contentType(ContentType.Application.Json)
                body = params
            }
        }
    }

    override suspend fun refresh(expiredAccessToken: String, refreshToken: String): Result0<String> {
        val params = mapOf(
            "expiredAccessToken" to expiredAccessToken,
            "refreshToken" to refreshToken
        )
        return executeResultSimple {
            client.post(GET_REFRESH) {
                contentType(ContentType.Application.Json)
                body = params
            }
        }
    }

    override suspend fun getClassmates(): Result0<List<Classmate>> {
        return executeResultObject {
            client.get(GET_CLASSMATES) {
                header("sessionId", sessionId)
            }
        }
    }

    override suspend fun getPortfolio(): Result0<MyPortfolio> {
        return executeResultObject {
            client.get(GET_PORTFOLIO) {
                header("sessionId", sessionId)
            }
        }
    }

    override suspend fun setPortfolio(myPortfolio: MyPortfolio): Result0<MyPortfolio> {
        return executeResultObject {
            client.post(GET_PORTFOLIO) {
                contentType(ContentType.Application.Json)
                header("sessionId", sessionId)
                body = myPortfolio
            }
        }
    }

    override suspend fun getDialogs(): Result0<List<DialogModel>> {
        return executeResultObject {
            client.get(GET_DIALOGS) {
                header("sessionId", sessionId)
            }
        }
    }

    override suspend fun getGradeSheet(guid: String): Result0<GradeSheet> {
        return executeResultObject {
            client.get(GET_GRADE_SHEET) {
                parameter("guid", guid)
                header("sessionId", sessionId)
            }
        }
    }

    override suspend fun getGradeSheetMarks(guid: String): Result0<List<GradeSheetMark>> {
        return executeResultObject {
            client.get(GET_GROUP_MARKS) {
                parameter("guid", guid)
                header("sessionId", sessionId)
            }
        }
    }

    override suspend fun getInfo(): Result0<Info> {
        return executeResultObject {
            client.get(GET_INFO) {
                header("sessionId", sessionId)
            }
        }
    }

    override suspend fun getMarks(): Result0<Marks> {
        return executeResultObject {
            client.get(GET_MARKS) {
                header("sessionId", sessionId)
            }
        }
    }

    override suspend fun getMessages(dialogKey: String): Result0<List<Message>> {
        return executeResultObject {
            client.get(GET_DIALOG) {
                header("sessionId", sessionId)
                parameter("dialogKey", dialogKey)
            }
        }
    }

    override suspend fun sendMessage(message: MessageSend): Result0<List<Message>> {
        return executeResultObject {
            client.post(SEND_MESSAGE) {
                header("sessionId", sessionId)
                contentType(ContentType.Application.Json)
                body = message
            }
        }
    }

    override suspend fun deleteMessage(removeKey: String): Result0<List<Message>> {
        return executeResultObject {
            client.delete(SEND_MESSAGE) {
                header("sessionId", sessionId)
                parameter("removeKey", removeKey)
            }
        }
    }

    override suspend fun getPayments(): Result0<Payments> {
        return executeResultObject {
            client.get(GET_PAYMENTS) {
                header("sessionId", sessionId)
            }
        }
    }

    override suspend fun getStatements(semester: String?): Result0<Statements> {
        return executeResultObject {
            client.get(GET_STATEMENTS) {
                parameter("semester", semester)
                header("sessionId", sessionId)
            }
        }
    }

    override suspend fun getStudents(searchQuery: String, page: Int): StudentsDto {
        return client.get<String>(GET_PORTFOLIOS) {
            parameter("searchQuery", searchQuery)
            parameter("page", page)
        }.toObject()
    }

    override suspend fun getTeachers(searchQuery: String, page: Int, sessionId: String): TeachersDto {
        return client.get<String>(GET_TEACHERS) {
            parameter("searchQuery", searchQuery)
            parameter("page", page)
            header("sessionId", sessionId)
        }.toObject()
    }

    private inline fun<reified T> executeResultObject(query: () -> String): Result0<T> {
        return try {
            query.invoke().jsonToResult()
        } catch (exception: Throwable) {
            Result0.Failure(exception)
        }
    }

    private inline fun executeResultSimple(query: () -> String): Result0<String> {
        return try {
            Result0.Success(query.invoke())
        } catch (exception: Throwable) {
            Result0.Failure(exception)
        }
    }

}