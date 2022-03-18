package com.mospolytech.features.base.core

import android.content.Context
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.serialization.Serializable

@Serializable
sealed class NativeText {
    @Serializable
    data class Simple(val text: String) : NativeText()
    @Serializable
    data class Resource(@StringRes val id: Int) : NativeText()
    @Serializable
    data class Plural(@PluralsRes val id: Int, val number: Int, val args: List<String>) : NativeText()
    @Serializable
    data class Arguments(@StringRes val id: Int, val args: List<String>) : NativeText() {
        constructor(@StringRes id: Int, vararg args: String) : this(id, args.toList())
    }
    @Serializable
    data class Multi(val text: List<NativeText>) : NativeText()
}

@JvmName("toString")
fun NativeText.toString(context: Context): String {
    return when (this) {
        is NativeText.Arguments -> context.getString(id, *args.toTypedArray())
        is NativeText.Multi -> {
            val builder = StringBuilder()
            for (t in text) {
                builder.append(t.toString(context))
            }
            builder.toString()
        }
        is NativeText.Plural -> context.resources.getQuantityString(id, number, *args.toTypedArray())
        is NativeText.Resource -> context.getString(id)
        is NativeText.Simple -> text
    }
}

@JvmName("toStringN")
fun NativeText?.toString(context: Context): String {
    return this?.toString(context) ?: ""
}

@Composable
fun NativeText?.toStringRemember(context: Context): String {
    return remember(this, context) { this?.toString(context) ?: "" }
}
