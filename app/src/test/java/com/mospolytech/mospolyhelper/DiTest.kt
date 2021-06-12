package com.mospolytech.mospolyhelper

import android.content.Context
import android.content.SharedPreferences
import android.content.res.AssetManager
import com.mospolytech.mospolyhelper.di.appModule
import com.mospolytech.mospolyhelper.di.diModules
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.check.checkModules
import org.koin.test.mock.MockProviderRule
import org.koin.test.mock.declareMock
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class DiTest : KoinTest {
    @get:Rule
    val mockProvider = MockProviderRule.create { clazz ->
        // Your way to build a Mock here
        Mockito.mock(clazz.java)
    }

    @Test
    fun `check MVVM hierarchy`() {
        val testModule = module {
            single<Context> { declareMock() }
            single<AssetManager> { declareMock() }
            single<SharedPreferences> { declareMock() }
        }

        checkModules {
            modules(testModule + diModules - appModule)
        }
    }
}