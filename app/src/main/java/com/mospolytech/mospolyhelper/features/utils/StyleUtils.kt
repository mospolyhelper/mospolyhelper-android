package com.mospolytech.mospolyhelper.features.utils

import android.content.Context
import android.content.res.ColorStateList
import android.util.TypedValue
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView


fun Context.getAttributeRes(attributeId: Int): Int? {
    val typedValue = TypedValue()
    val res = theme.resolveAttribute(attributeId, typedValue, true)
    return if (res) typedValue.resourceId else null
}
fun Context.getAttributeColor(attributeId: Int): Int? {
    val typedValue = TypedValue()
    val res = theme.resolveAttribute(attributeId, typedValue, true)
    val colorRes = if (res) typedValue.resourceId else null
    return if (colorRes != null) getColor(colorRes) else null
}


fun Fragment.getColor(@ColorRes id: Int): Int =
    requireContext().getColor(id)
fun Fragment.getColorStateList(@ColorRes id: Int): ColorStateList =
    requireContext().getColorStateList(id)
fun Fragment.getAttributeRes(attributeId: Int): Int? =
    requireContext().getAttributeRes(attributeId)
fun Fragment.getAttributeColor(attributeId: Int): Int? =
    requireContext().getAttributeColor(attributeId)


fun RecyclerView.ViewHolder.getColor(@ColorRes id: Int): Int =
    itemView.context.getColor(id)
fun RecyclerView.ViewHolder.getColorStateList(@ColorRes id: Int): ColorStateList =
    itemView.context.getColorStateList(id)
fun RecyclerView.ViewHolder.getString(@StringRes resId: Int): String =
    itemView.context.getString(resId)
fun RecyclerView.ViewHolder.getString(@StringRes resId: Int, vararg formatArgs: Any): String =
    itemView.context.getString(resId, *formatArgs)
fun RecyclerView.ViewHolder.getAttributeRes(attributeId: Int): Int? =
    itemView.context.getAttributeRes(attributeId)
fun RecyclerView.ViewHolder.getAttributeColor(attributeId: Int): Int? =
    itemView.context.getAttributeColor(attributeId)