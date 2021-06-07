package com.mospolytech.mospolyhelper.features.ui.utilities.settings

import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceScreen
import androidx.preference.internal.PreferenceImageView
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.databinding.FragmentSettingsBinding
import com.mospolytech.mospolyhelper.features.ui.main.MainActivity
import com.mospolytech.mospolyhelper.utils.safe

class SettingsFragment : PreferenceFragmentCompat(),
    PreferenceFragmentCompat.OnPreferenceStartScreenCallback {

    override fun getCallbackFragment() = this

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)
    }

    private val viewModel by viewModels<SettingsViewModel>()
    private val viewBinding by viewBinding(FragmentSettingsBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setIconsTint()

        val toolbar = view.findViewById<Toolbar>(R.id.bottomAppBar)
        toolbar.title = getString(R.string.settings_title)
        (activity as MainActivity).setSupportActionBar(toolbar)
        (activity as MainActivity).supportActionBar!!.setDisplayShowTitleEnabled(true)

        if (preferenceScreen.key == "MainScreen") {
            toolbar.navigationIcon = null
        } else {
            toolbar.setNavigationIcon(R.drawable.ic_round_arrow_back_24)
            toolbar.setNavigationOnClickListener {
                requireActivity().onBackPressed()
            }
            (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
            (activity as MainActivity).supportActionBar?.setHomeButtonEnabled(true)
        }
    }

    private fun setIconsTint() {
        val rv = requireView().findViewById<RecyclerView>(androidx.preference.R.id.recycler_view)
        rv?.viewTreeObserver?.addOnDrawListener {
            rv.children.forEach { pref ->
                val icon = pref.findViewById<View>(android.R.id.icon) as? PreferenceImageView
                icon?.let {
                    if (it.tag != "painted") {
                        it.setColorFilter(
                            ContextCompat.getColor(requireContext(), R.color.text_color_primary),
                            PorterDuff.Mode.SRC_IN
                        )
                        it.tag = "painted"
                    }
                }
            }
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
