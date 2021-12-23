package com.mospolytech.features.base

import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class BaseViewModel<S: State<*>>(
    initialState: S,
    protected open val navController: NavController,
//    initialData: D
): ViewModel() {

    private val _state = MutableStateFlow(initialState)
    val state = _state.asStateFlow()

//    private var initialized = false

//    init {
//        initializeIfNeeded(initialData)
//    }
//
//    fun initializeIfNeeded(data: D) {
//        if (!initialized) {
//            initialized = true
//            init(data)
//        }
//    }
//
//    open fun init(data: D) { }
}