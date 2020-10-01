package com.mospolytech.mospolyhelper.features.ui.settings.schedule_appwidget

import android.os.Bundle
import android.view.View
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceScreen
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomappbar.BottomAppBar
import com.mospolytech.mospolyhelper.NavGraphDirections
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.features.ui.main.MainActivity
import com.mospolytech.mospolyhelper.features.ui.settings.SettingsViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ScheduleAppWidgetPreferences : PreferenceFragmentCompat(),
    PreferenceFragmentCompat.OnPreferenceStartScreenCallback {

    override fun getCallbackFragment() = this

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomAppBar = view.findViewById<BottomAppBar>(R.id.bottomAppBar)
        bottomAppBar.title = getString(R.string.settings_title)
        (activity as MainActivity).setSupportActionBar(bottomAppBar)
        (activity as MainActivity).supportActionBar!!.setDisplayShowTitleEnabled(false)

        bottomAppBar.setNavigationIcon(R.drawable.ic_round_arrow_back_24)
        bottomAppBar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as MainActivity).supportActionBar?.setHomeButtonEnabled(true)
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        return super.onPreferenceTreeClick(preference)
    }

    override fun onPreferenceStartScreen(
        caller: PreferenceFragmentCompat,
        pref: PreferenceScreen
    ) = false
}
