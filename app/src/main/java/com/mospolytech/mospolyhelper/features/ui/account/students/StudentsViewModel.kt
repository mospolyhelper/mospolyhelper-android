package com.mospolytech.mospolyhelper.features.ui.account.students

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import com.mospolytech.mospolyhelper.domain.account.students.model.Student
import com.mospolytech.mospolyhelper.domain.account.students.usecase.StudentsUseCase
import com.mospolytech.mospolyhelper.features.ui.account.students.other.FilterEntity
import com.mospolytech.mospolyhelper.features.ui.common.Mediator
import com.mospolytech.mospolyhelper.features.ui.common.ViewModelBase
import com.mospolytech.mospolyhelper.features.ui.common.ViewModelMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.component.KoinComponent

class StudentsViewModel(
    mediator: Mediator<String, ViewModelMessage>,
    private val useCase: StudentsUseCase
) : ViewModelBase(mediator, StudentsViewModel::class.java.simpleName), KoinComponent {

    fun fetchStudents(query: String, filters: FilterEntity): Flow<PagingData<Student>> {
        return useCase.getInfo(query).map { pagingData ->
            pagingData.filter { student ->
                var course = true
                var form = true
                var type = true
                if (filters.courses.isNotEmpty()) {
                    if (!filters.courses.contains(student.course)) {
                        course = false
                    }
                }
                if (filters.form.isNotEmpty()) {
                    if (!filters.form.contains(student.educationForm)) {
                        form = false
                    }
                }
                if (filters.type.isNotEmpty()) {
                    type = false
                    filters.type.forEach {
                        if (student.direction.contains(it, true)) type = true
                    }
                    if (!type) {
                        filters.type.forEach {
                            if (student.direction.contains(it.replace(".", ""), true) && it != ".02.")
                                type = true
                        }
                    }
                }
                course && form && type
            }
        }.cachedIn(viewModelScope)
    }
}