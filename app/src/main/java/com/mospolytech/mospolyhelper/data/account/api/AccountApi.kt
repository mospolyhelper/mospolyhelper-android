package com.mospolytech.mospolyhelper.data.account.api

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
import com.mospolytech.mospolyhelper.utils.Result0

interface AccountApi {
    suspend fun getApplications(): Result0<List<Application>>
    suspend fun auth(login: String, password: String): Result0<JwtModel>
    suspend fun refresh(expiredAccessToken: String, refreshToken: String): Result0<String>
    suspend fun getClassmates(): Result0<List<Classmate>>
    suspend fun getPortfolio(): Result0<MyPortfolio>
    suspend fun setPortfolio(myPortfolio: MyPortfolio): Result0<MyPortfolio>
    suspend fun getDialogs(): Result0<List<DialogModel>>
    suspend fun getGradeSheet(guid: String): Result0<GradeSheet>
    suspend fun getGradeSheetMarks(guid: String): Result0<List<GradeSheetMark>>
    suspend fun getInfo(): Result0<Info>
    suspend fun getMarks(): Result0<Marks>
    suspend fun getMessages(dialogKey: String): Result0<List<Message>>
    suspend fun sendMessage(message: MessageSend): Result0<List<Message>>
    suspend fun deleteMessage(removeKey: String): Result0<List<Message>>
    suspend fun getPayments(): Result0<Payments>
    suspend fun getStatements(semester: String?): Result0<Statements>
    suspend fun getStudents(searchQuery: String, page: Int): StudentsDto
    suspend fun getTeachers(searchQuery: String, page: Int, sessionId: String): TeachersDto
}