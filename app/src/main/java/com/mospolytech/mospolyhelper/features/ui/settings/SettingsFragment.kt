package com.mospolytech.mospolyhelper.features.ui.settings

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceScreen
import com.google.android.material.bottomappbar.BottomAppBar
import com.mospolytech.mospolyhelper.NavGraphDirections
import com.mospolytech.mospolyhelper.features.ui.main.MainActivity

import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.utils.safe

class SettingsFragment : PreferenceFragmentCompat(),
    PreferenceFragmentCompat.OnPreferenceStartScreenCallback {

    override fun getCallbackFragment() = this

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)
    }

    private val viewModel by viewModels<SettingsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomAppBar = view.findViewById<BottomAppBar>(R.id.bottomAppBar)
        bottomAppBar.title = getString(R.string.settings_title)
        (activity as MainActivity).setSupportActionBar(bottomAppBar)
        (activity as MainActivity).supportActionBar!!.setDisplayShowTitleEnabled(false)

        if (preferenceScreen.key == "MainScreen") {
            bottomAppBar.setNavigationIcon(R.drawable.ic_menu_24px)
            bottomAppBar.setNavigationOnClickListener {
                findNavController().safe {
                    navigate(NavGraphDirections.actionGlobalMainMenuFragment())
                }
            }
        } else {
            bottomAppBar.setNavigationIcon(R.drawable.ic_round_arrow_back_24)
            bottomAppBar.setNavigationOnClickListener {
                requireActivity().onBackPressed()
            }
            (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
            (activity as MainActivity).supportActionBar?.setHomeButtonEnabled(true)
        }
    }


    override fun onPreferenceStartScreen(
        caller: PreferenceFragmentCompat,
        pref: PreferenceScreen
    ): Boolean {
        val args = Bundle()
        args.putString(ARG_PREFERENCE_ROOT, pref.key)
        if (pref.key == "ScheduleAppWidgetScreen") {
            caller.findNavController().safe {
                navigate(R.id.action_settingsFragment_to_scheduleAppWidgetPreferences, args)
            }
        } else {
            caller.findNavController().safe {
                navigate(R.id.action_settingsFragment_self, args)
            }
        }
        return true
    }
}
