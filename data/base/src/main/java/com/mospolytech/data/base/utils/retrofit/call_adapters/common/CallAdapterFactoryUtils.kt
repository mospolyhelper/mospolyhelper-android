package com.mospolytech.data.base.utils.retrofit.call_adapters.common

import android.util.Log
import kotlinx.coroutines.flow.Flow
import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

abstract class BaseCallAdapterFactory : CallAdapter.Factory() {
    fun checkCall(returnType: Type): Type? {
        // suspend functions wrap the response type in `Call`
        if (getRawType(returnType) != Call::class.java) {
            Log.e("CallAdapter.Factory", "Return type of the function is not Call. Try add suspend")
            return null
        }

        check(returnType is ParameterizedType) {
            "return type must be parameterized as Call<NetworkResponse<<Foo>> or Call<NetworkResponse<out Foo>>"
        }

        return getParameterUpperBound(0, returnType)
    }

    fun checkFlow(returnType: Type): Type? {
        if (getRawType(returnType) != Flow::class.java) {
            return null
        }

        check(returnType is ParameterizedType) {
            "return type must be parameterized as Call<NetworkResponse<<Foo>> or Call<NetworkResponse<out Foo>>"
        }

        return getParameterUpperBound(0, returnType)
    }

    fun checkResult(returnType: Type): Type? {
        if (getRawType(returnType) != Result::class.java) {
            return null
        }

        check(returnType is ParameterizedType) {
            "return type must be parameterized as Call<NetworkResponse<<Foo>> or Call<NetworkResponse<out Foo>>"
        }

        return getParameterUpperBound(0, returnType)
    }
//
//    fun envelopeType(returnType: Type): Type {
//        return object : ParameterizedType {
//            override fun getRawType() = Response::class.java
//            override fun getOwnerType() = null
//            override fun getActualTypeArguments() = arrayOf(returnType)
//        }
//    }
}