package com.mospolytech.data.base.utils.retrofit.call_adapters.common

import com.mospolytech.data.base.utils.retrofit.registerCallback
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.Type
import kotlin.coroutines.resume

class FlowCallAdapter<T : Any>(
    private val successType: Type,
    private val mapper: ResponseMapper<T>
) : CallAdapter<T, Flow<Result<T>>> {
    override fun responseType() = successType
    override fun adapt(call: Call<T>): Flow<Result<T>> = flow {
        emit(suspendAdapt(call))
    }

    private suspend fun suspendAdapt(call: Call<T>): Result<T> =
        suspendCancellableCoroutine { continuation ->
            call.registerCallback(
                onSuccess = { response ->
                    continuation.resume(mapper.mapResponse(response))
                },
                onFailure = {
                    continuation.resume(mapper.mapFailure(it))
                }
            )

            continuation.invokeOnCancellation {
                try {
                    call.cancel()
                } catch (e: Exception) { }
            }
        }
}