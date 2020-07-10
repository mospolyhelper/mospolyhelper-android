package com.mospolytech.mospolyhelper

import android.Manifest
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.preference.PreferenceManager
import com.google.android.material.navigation.NavigationView
import com.mospolytech.mospolyhelper.utils.PreferenceKeys
import kotlinx.coroutines.async
import kotlinx.coroutines.delay


class MainActivity() : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {
    companion object {
        const val launchCode = 0
    }
    private var doubleBackToExitPressedOnce = false

    private val viewModel by viewModels<MainViewModel>()
    private val drawerLayout by lazy { findViewById<DrawerLayout>(R.id.drawer_layout) }
    private val navHostFragment by lazy {
        supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
    }
    private val navController by lazy { navHostFragment.navController }

    private val navigationView by lazy { findViewById<NavigationView>(R.id.nav_view) }


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
                prefs.getInt(PreferenceKeys.FirstLaunch, launchCode)
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
        navigationView.setCheckedItem(R.id.nav_schedule)
        navigationView.setNavigationItemSelectedListener {
            val action = navController.graph.getAction(it.itemId)
            val currentDestination = navController.currentBackStackEntry?.destination
            if (action != null && currentDestination!= null && action.destinationId != currentDestination.id) {
                navController.navigate(it.itemId)
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        doubleBackToExitPressedOnce = false
        PreferenceManager.getDefaultSharedPreferences(this)
            .registerOnSharedPreferenceChangeListener(this)
    }

    fun onBackPressedCustom() {
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        val settingsDrawer = findViewById<DrawerLayout>(R.id.drawer_layout_schedule)
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
            return
        }
        if (settingsDrawer != null && settingsDrawer.isDrawerOpen(GravityCompat.END)) {
            settingsDrawer.closeDrawer(GravityCompat.END)
            return
        }

        if (navController.previousBackStackEntry == null) {
            if (doubleBackToExitPressedOnce) {
                finish()
            } else {
                Toast.makeText(this, R.string.click_back_again, Toast.LENGTH_SHORT).show()
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
            PreferenceKeys.ScheduleShowColoredLessons ->
                viewModel.changeShowEmptyLessons(sharedPreferences.getBoolean(key, false))
            PreferenceKeys.ScheduleShowEmptyLessons ->
                viewModel.changeShowColoredLessons(sharedPreferences.getBoolean(key, false))
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

