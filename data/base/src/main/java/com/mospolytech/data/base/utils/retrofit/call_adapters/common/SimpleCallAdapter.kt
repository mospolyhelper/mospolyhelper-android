package com.mospolytech.data.base.utils.retrofit.call_adapters.common

import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.Type

class SimpleCallAdapter<TIn : Any, TOut : Any>(
    private val successType: Type,
    private val adaptCall: (call: Call<TIn>) -> Call<TOut>
) : CallAdapter<TIn, Call<TOut>> {
    override fun responseType() = successType
    override fun adapt(call: Call<TIn>) = adaptCall(call)
}