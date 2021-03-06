package com.mospolytech.mospolyhelper.features.ui.main

import android.Manifest
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.utils.PreferenceKeys
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import org.koin.core.KoinComponent
import org.koin.core.inject


class MainActivity : AppCompatActivity(), KoinComponent, SharedPreferences.OnSharedPreferenceChangeListener {
    companion object {
        const val launchCode = 0
    }
    private var doubleBackToExitPressedOnce = false

    private val viewModel by inject<MainViewModel>()
    private val navHostFragment by lazy {
        supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
    }
    private val navController by lazy { navHostFragment.navController }
    private lateinit var navigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {

        if (savedInstanceState == null) {
            val prefs = PreferenceManager.getDefaultSharedPreferences(this)
            AppCompatDelegate.setDefaultNightMode(
                if (prefs.getBoolean(PreferenceKeys.NightMode, false))
                    AppCompatDelegate.MODE_NIGHT_YES
                else
                    AppCompatDelegate.MODE_NIGHT_NO
            )
            val firstLaunch = try {
                prefs.getInt(PreferenceKeys.FirstLaunch,
                    launchCode
                )
            } catch (e: Exception) {
                launchCode
            }
            if (firstLaunch == launchCode) {
                prefs.edit().clear().apply()
                prefs.edit().putInt(PreferenceKeys.FirstLaunch, launchCode + 1).apply()
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


        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.INTERNET),
            123
        )

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

    fun onBackPressedCustom() {
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

    override fun onResume() {
        super.onResume()

        // TODO: check event subscription
        navigationView = findViewById(R.id.nav_view)
        navigationView.setOnNavigationItemSelectedListener {
            val action = navController.graph.getAction(it.itemId)
            val currentDestination = navController.currentBackStackEntry?.destination
            if (action != null && currentDestination!= null && action.destinationId != currentDestination.id) {
                viewModel.currentFragmentNavId.value = it.itemId
                navController.navigate(it.itemId)
            }
            true
        }
    }

    override fun onDestroy() {
        PreferenceManager.getDefaultSharedPreferences(this)
            .unregisterOnSharedPreferenceChangeListener(this)
        super.onDestroy()
    }
}

