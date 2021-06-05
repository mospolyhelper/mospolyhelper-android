package com.mospolytech.mospolyhelper.features.ui.main

import android.Manifest
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.preference.PreferenceManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.data.core.local.SharedPreferencesDataSource
import com.mospolytech.mospolyhelper.databinding.ActivityMainBinding
import com.mospolytech.mospolyhelper.features.utils.PermissionRequestCodes
import com.mospolytech.mospolyhelper.utils.PreferenceKeys
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.lang.ClassCastException


class MainActivity : AppCompatActivity(), KoinComponent, SharedPreferences.OnSharedPreferenceChangeListener {
    private var doubleBackToExitPressedOnce = false

    private val clearVersion = 1

    private val viewModel by inject<MainViewModel>()
    private val viewBinding by viewBinding(ActivityMainBinding::bind)
    private val navHostFragment by lazy {
        supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
    }
    private val navController by lazy { navHostFragment.navController }

    override fun onCreate(savedInstanceState: Bundle?) {

        if (savedInstanceState == null) {
            val prefs = SharedPreferencesDataSource(
                PreferenceManager.getDefaultSharedPreferences(this)
            )
            AppCompatDelegate.setDefaultNightMode(
                if (prefs.get(PreferenceKeys.NightMode, false))
                    AppCompatDelegate.MODE_NIGHT_YES
                else
                    AppCompatDelegate.MODE_NIGHT_NO
            )
            val clearedVersion = try {
                prefs.get(PreferenceKeys.FirstLaunch, 0)
            } catch (e: ClassCastException) {
                0
            }
            if (clearedVersion < clearVersion) {
                prefs.clear()
                prefs.set(PreferenceKeys.FirstLaunch, clearVersion)
            }
        }
        setTheme(R.style.AppTheme_NoActionBar)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            viewModel.currentFragmentNavId.value = R.id.nav_schedule
        } else {
            viewModel.currentFragmentNavId.value = savedInstanceState.getInt("menuItemId", R.id.nav_schedule)
        }
        if (viewModel.currentFragmentNavId.value != -1) {
            viewBinding.navView.selectedItemId = viewModel.currentFragmentNavId.value
        }
        viewBinding.navView.setOnNavigationItemSelectedListener {
            val action = navController.graph.getAction(it.itemId)
            val currentDestination = navController.currentBackStackEntry?.destination
            if (action != null && currentDestination != null && action.destinationId != currentDestination.id) {
                viewModel.currentFragmentNavId.value = it.itemId
                navController.navigate(it.itemId)
            }
            true
        }

        onBackPressedDispatcher.addCallback(this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    onBackPressedCustom()
                }
            })

        doubleBackToExitPressedOnce = false
        PreferenceManager.getDefaultSharedPreferences(this)
            .registerOnSharedPreferenceChangeListener(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("menuItemId", viewModel.currentFragmentNavId.value)
    }

    private fun onBackPressedCustom() {
        if (navController.previousBackStackEntry == null) {
            if (doubleBackToExitPressedOnce) {
                finish()
            } else {
                Toast.makeText(this,
                    R.string.click_back_again, Toast.LENGTH_SHORT).show()
                this.doubleBackToExitPressedOnce = true
                lifecycleScope.async {
                    delay(2000)
                    doubleBackToExitPressedOnce = false
                }
            }
        } else {
            navController.popBackStack()
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        when (key) {
            PreferenceKeys.NightMode -> {
                AppCompatDelegate.setDefaultNightMode(
                    if (sharedPreferences.getBoolean(key, false)) {
                        AppCompatDelegate.MODE_NIGHT_YES
                    } else {
                        AppCompatDelegate.MODE_NIGHT_NO
                    }
                )

                delegate.applyDayNight()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            navController.navigateUp()
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onDestroy() {
        PreferenceManager.getDefaultSharedPreferences(this)
            .unregisterOnSharedPreferenceChangeListener(this)
        super.onDestroy()
    }
}

