package com.mospolytech.mospolyhelper.utils

import android.view.View

fun View.setSafeOnClickListener(interval: Int = 1000, onSafeClick: (View) -> Unit) {
    setOnClickListener(SafeClickListener(interval) {
        onSafeClick(it)
    })
}

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.hide() {
    this.visibility = View.INVISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}