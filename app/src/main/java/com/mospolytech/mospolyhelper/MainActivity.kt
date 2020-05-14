package com.mospolytech.mospolyhelper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}

val Any.TAG: String
    get() {
        val tag = this::class.java.simpleName
        return if (tag.length <= 23) tag else tag.substring(0, 23)
    }
