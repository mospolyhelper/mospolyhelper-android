package com.mospolytech.mospolyhelper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.BoringLayout
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.Fragment
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun changeFragment(fragment: Fragment, flag: Boolean): Unit = TODO()
}

val Any.TAG: String
    get() {
        val tag = this::class.java.simpleName
        return if (tag.length <= 23) tag else tag.substring(0, 23)
    }

