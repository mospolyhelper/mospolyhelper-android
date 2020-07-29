package com.mospolytech.mospolyhelper.ui.settings

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceScreen
import com.google.android.material.bottomappbar.BottomAppBar
import com.mospolytech.mospolyhelper.NavGraphDirections
import com.mospolytech.mospolyhelper.ui.main.MainActivity

import com.mospolytech.mospolyhelper.R

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

        val bottomAppBar = view.findViewById<BottomAppBar>(R.id.bottomAppBar)
        bottomAppBar.title = getString(R.string.settings_title)
        (activity as MainActivity).setSupportActionBar(bottomAppBar)
        (activity as MainActivity).supportActionBar!!.setDisplayShowTitleEnabled(false)

        if (preferenceScreen.key == "MainScreen") {
            bottomAppBar.setNavigationIcon(R.drawable.ic_menu_24px)
            bottomAppBar.setNavigationOnClickListener {
                findNavController().navigate(NavGraphDirections.actionGlobalMainMenuFragment())
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
