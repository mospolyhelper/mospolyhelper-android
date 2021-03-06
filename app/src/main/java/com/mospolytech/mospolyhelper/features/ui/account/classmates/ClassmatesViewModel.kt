package com.mospolytech.mospolyhelper.features.ui.account.classmates

import com.mospolytech.mospolyhelper.domain.account.classmates.model.Classmate
import com.mospolytech.mospolyhelper.domain.account.classmates.usecase.ClassmatesUseCase
import com.mospolytech.mospolyhelper.domain.account.info.model.Info
import com.mospolytech.mospolyhelper.domain.account.info.usecase.InfoUseCase
import com.mospolytech.mospolyhelper.features.ui.common.Mediator
import com.mospolytech.mospolyhelper.features.ui.common.ViewModelBase
import com.mospolytech.mospolyhelper.features.ui.common.ViewModelMessage
import com.mospolytech.mospolyhelper.utils.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import org.koin.core.KoinComponent

class ClassmatesViewModel(
    mediator: Mediator<String, ViewModelMessage>,
    private val useCase: ClassmatesUseCase
) : ViewModelBase(mediator, ClassmatesViewModel::class.java.simpleName), KoinComponent {

    val classmates = MutableStateFlow<Result<List<Classmate>>>(Result.loading())


    suspend fun downloadInfo() {
        useCase.getInfo().collect {
            classmates.value = it
        }
    }

    suspend fun getInfo() {
        useCase.getLocalInfo().collect {
            classmates.value = it
        }
        //classmates.value = Result.loading()
        useCase.getInfo().collect {
            classmates.value = it
        }
    }

}