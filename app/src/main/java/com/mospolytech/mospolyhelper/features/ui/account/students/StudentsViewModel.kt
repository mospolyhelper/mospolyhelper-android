package com.mospolytech.mospolyhelper.features.ui.account.students

import androidx.lifecycle.viewModelScope
import androidx.paging.PagedList
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mospolytech.mospolyhelper.domain.account.marks.model.Marks
import com.mospolytech.mospolyhelper.domain.account.students.model.Student
import com.mospolytech.mospolyhelper.domain.account.students.model.StudentsSearchResult
import com.mospolytech.mospolyhelper.domain.account.students.usecase.StudentsUseCase
import com.mospolytech.mospolyhelper.domain.schedule.usecase.ScheduleUseCase
import com.mospolytech.mospolyhelper.features.ui.common.Mediator
import com.mospolytech.mospolyhelper.features.ui.common.ViewModelBase
import com.mospolytech.mospolyhelper.features.ui.common.ViewModelMessage
import com.mospolytech.mospolyhelper.features.ui.schedule.ScheduleViewModel
import com.mospolytech.mospolyhelper.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import org.koin.core.KoinComponent

class StudentsViewModel(
    mediator: Mediator<String, ViewModelMessage>,
    private val useCase: StudentsUseCase
) : ViewModelBase(mediator, StudentsViewModel::class.java.simpleName), KoinComponent {

    fun fetchStudents(query: String): Flow<PagingData<Student>> {
        return useCase.getInfo(query)
    }
}