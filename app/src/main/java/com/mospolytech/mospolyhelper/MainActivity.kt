package com.mospolytech.mospolyhelper

import android.Manifest
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.google.android.material.navigation.NavigationView
import com.mospolytech.mospolyhelper.ui.addresses.AddressesFragment
import com.mospolytech.mospolyhelper.ui.common.FragmentBase
import com.mospolytech.mospolyhelper.ui.common.Fragments
import com.mospolytech.mospolyhelper.ui.common.interfaces.IFragmentBase
import com.mospolytech.mospolyhelper.ui.deadlines.DeadlineFragment
import com.mospolytech.mospolyhelper.ui.schedule.ScheduleFragment
import com.mospolytech.mospolyhelper.ui.settings.SettingsFragment
import com.mospolytech.mospolyhelper.utils.AssetProvider
import com.mospolytech.mospolyhelper.utils.ContextProvider
import com.mospolytech.mospolyhelper.utils.PreferencesConstants
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay


class MainActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {
    companion object {
        const val launchCode = 0
    }

    var prevFragment: IFragmentBase? = null
    var currFragment: IFragmentBase? = null

    var clickBackAgain: String = ""
    var doubleBackToExitPressedOnce = false

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {

        if (savedInstanceState == null) {
            val prefs = PreferenceManager.getDefaultSharedPreferences(this)
            AppCompatDelegate.setDefaultNightMode(
                if (prefs.getBoolean("NightMode", false))
                    AppCompatDelegate.MODE_NIGHT_YES
                else
                    AppCompatDelegate.MODE_NIGHT_NO
            )
            // fix on release
            var firstLaunch = launchCode
            try {
                firstLaunch = prefs.getInt(PreferencesConstants.FirstLaunch, launchCode)
            } catch (ex: Exception) {
                firstLaunch = launchCode
            }
            if (firstLaunch == launchCode) {
                prefs.edit().clear().apply()
                prefs.edit().putInt(PreferencesConstants.FirstLaunch, launchCode + 1).apply()
            }
            //this.ScheduleVmPreloadTask = Task.Run(() => PrepareSchdeuleVm(prefs));
        }
        setTheme(R.style.AppTheme_NoActionBar)

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        ContextProvider.context = this
        AssetProvider.assetManager = assets

        if (savedInstanceState == null) {
            changeFragment(ScheduleFragment.newInstance(), true)
        } else {
            // TODO: Fix stack
            //ChangeFragment(this.SupportFragmentManager.GetBackStackEntryAt(0)., Fragments.ScheduleMain, false)
        }

//        ActivityCompat.requestPermissions(this,
//            new string[] { Android.Manifest.Permission.Internet }, 123)

        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.INTERNET),
            123
        )


        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener {
            onNavigationItemSelected(it)
        }
        navigationView.setCheckedItem(R.id.nav_schedule)
        clickBackAgain = getString(R.string.click_back_again)
        doubleBackToExitPressedOnce = false
        PreferenceManager.getDefaultSharedPreferences(this)
            .registerOnSharedPreferenceChangeListener(this)
    }

    override fun onResume() {
        super.onResume()
        ContextProvider.context = this
    }

    override fun onBackPressed() {
        var actionDone = false

        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        val settingsDrawer = findViewById<DrawerLayout>(R.id.drawer_layout_schedule)
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
            actionDone = true
        }
        if (settingsDrawer != null && settingsDrawer.isDrawerOpen(GravityCompat.END)) {
            settingsDrawer.closeDrawer(GravityCompat.END)
            actionDone = true
        }

        if (supportFragmentManager.backStackEntryCount != 0) {
            super.onBackPressed()
            actionDone = true
        }

        if (!actionDone) {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed()
                finish()
            } else {
                Toast.makeText(this, clickBackAgain, Toast.LENGTH_SHORT).show()
                this.doubleBackToExitPressedOnce = true
            }
            updateExitFlag()
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        when (key) {
            PreferencesConstants.ScheduleShowColoredLessons ->
                viewModel.changeShowEmptyLessons(sharedPreferences.getBoolean(key, false))
            PreferencesConstants.ScheduleShowEmptyLessons ->
                viewModel.changeShowColoredLessons(sharedPreferences.getBoolean(key, false))
            "NightMode" -> {
                AppCompatDelegate.setDefaultNightMode(
                    if (sharedPreferences.getBoolean(key, false) )
                        AppCompatDelegate.MODE_NIGHT_YES
                    else
                        AppCompatDelegate.MODE_NIGHT_NO
                )

                delegate.applyDayNight()
            }
        }
    }

    fun updateExitFlag() {
        GlobalScope.async {
            delay(2000)
            doubleBackToExitPressedOnce = false
        }
    }

    fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        var fragmentId = Fragments.Other
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)

        var fragmentCreator: (() -> IFragmentBase)? = null
        when (id) {
            R.id.nav_schedule -> {
                fragmentId = Fragments.ScheduleMain
                fragmentCreator = ScheduleFragment.Companion::newInstance
            }
            R.id.nav_buildings -> {
                fragmentId = Fragments.Addresses
                fragmentCreator = AddressesFragment.Companion::newInstance
            }
            R.id.nav_settings -> {
                fragmentId = Fragments.Settings
                fragmentCreator = SettingsFragment.Companion::newInstance
            }
            R.id.nav_deadlines -> {
                fragmentId = Fragments.Deadlines
                fragmentCreator = DeadlineFragment.Companion::newInstance
            }
        }
        if (currFragment?.fragmentType == fragmentId) {
            drawer.closeDrawer(GravityCompat.START)
            return false
        }
//        if (fragmentId == Fragments.Other) {
//            //this.logger.Warn("{ItemId} doesn't have own Fragments enum id", item.ItemId);
//        }
        if (fragmentCreator == null) {
            drawer.closeDrawer(GravityCompat.START)
            return false
        }
        changeFragment(fragmentCreator(), true)
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    fun changeFragment(fragment: IFragmentBase, disposePrevious: Boolean) {
        prevFragment = supportFragmentManager.findFragmentById(R.id.frame_schedule) as IFragmentBase?
        if (disposePrevious) {
            if (prevFragment != null) {
                //this.prevFragment.Fragment.Dispose();
            }
            supportFragmentManager.beginTransaction().replace(R.id.frame_schedule, fragment.fragment).commit()
        } else {
            supportFragmentManager.beginTransaction().add(R.id.frame_schedule, fragment.fragment)
                .addToBackStack(null).commit()
        }
        currFragment = fragment
    }


    override fun onDestroy() {
        PreferenceManager.getDefaultSharedPreferences(this)
            .unregisterOnSharedPreferenceChangeListener(this)
        super.onDestroy()
    }

    override fun onSupportNavigateUp(): Boolean {
        super.onBackPressed()
        return true
    }
}






val Any.TAG: String
    get() {
        val tag = this::class.java.simpleName
        return if (tag.length <= 23) tag else tag.substring(0, 23)
    }

