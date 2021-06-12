package com.mospolytech.mospolyhelper

import android.content.Context
import android.content.SharedPreferences
import android.content.res.AssetManager
import com.google.common.truth.Truth.assertThat
import com.mospolytech.mospolyhelper.di.appModule
import com.mospolytech.mospolyhelper.di.diModules
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.dsl.module
import org.koin.test.check.checkModules
import org.mockito.Mock
import org.mockito.runners.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
class DiTest {
    @Mock
    private lateinit var mockContext: Context
    @Mock
    private lateinit var mockAssetManager: AssetManager
    @Mock
    private lateinit var mockSharedPreferences: SharedPreferences

    @Test
    fun `check MVVM hierarchy`() {
        val testModule = module {
            single<Context> { mockContext }
            single<AssetManager> { mockAssetManager }
            single<SharedPreferences> { mockSharedPreferences }
        }

        checkModules {
            modules(testModule + diModules - appModule)
        }
    }
}