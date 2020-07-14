package com.mospolytech.mospolyhelper.ui.settings

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceScreen
import com.mospolytech.mospolyhelper.MainActivity

import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.ui.schedule.ScheduleFragmentDirections

class SettingsFragment : PreferenceFragmentCompat(),
    PreferenceFragmentCompat.OnPreferenceStartScreenCallback {
    companion object {
        fun newInstance() = SettingsFragment()
    }

    override fun getCallbackFragment() = this

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)
    }

    private val viewModel by viewModels<SettingsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = getString(R.string.settings_title)
        (activity as MainActivity).setSupportActionBar(toolbar)
        if (preferenceScreen.key == "MainScreen") {
            val drawer = requireActivity().findViewById<DrawerLayout>(R.id.drawer_layout)
            val toggle = ActionBarDrawerToggle(
                activity, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close
            )
            drawer.addDrawerListener(toggle)
            toggle.syncState()
            toggle.isDrawerIndicatorEnabled = true
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        } else {
            (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
            (activity as MainActivity).supportActionBar?.setHomeButtonEnabled(true)
            val drawer = requireActivity().findViewById<DrawerLayout>(R.id.drawer_layout)
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // TODO: Use the ViewModel
    }

    override fun onPreferenceStartScreen(
        caller: PreferenceFragmentCompat,
        pref: PreferenceScreen
    ): Boolean {
        val args = Bundle()
        args.putString(ARG_PREFERENCE_ROOT, pref.key)
        //val action = SettingsFragmentDirections.actionSettingsFragmentSelf()
        caller.findNavController().navigate(R.id.action_settingsFragment_self, args)
        return true
    }
}
