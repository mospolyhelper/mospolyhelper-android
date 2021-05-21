package com.mospolytech.mospolyhelper.features.ui.utilities.menu

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.view.menu.MenuBuilder
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.databinding.FragmentMenuUtilitiesBinding
import com.mospolytech.mospolyhelper.utils.safe

class UtilitiesMenuFragment : Fragment(R.layout.fragment_menu_utilities) {

    private val viewBinding by viewBinding(FragmentMenuUtilitiesBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setMenu()
    }

    @SuppressLint("RestrictedApi")
    private fun setMenu() {
        viewBinding.listMenu.layoutManager = GridLayoutManager(context, 2)
        val menu = MenuBuilder(context)
        requireActivity().menuInflater.inflate(R.menu.menu_utilities, menu)
        val adapter = MenuAdapter(menu)
        adapter.onItemMenuClick += {
            when (it) {
                R.id.nav_settings -> findNavController().safe {
                    navigate(
                        UtilitiesMenuFragmentDirections
                            .actionUtilitiesMenuFragmentToSettingsFragment()
                    )
                }
                R.id.nav_addresses -> findNavController().safe {
                    navigate(
                        UtilitiesMenuFragmentDirections
                            .actionUtilitiesMenuFragmentToAddressesFragment()
                    )
                }
                R.id.nav_deadlines -> findNavController().safe {
                    navigate(
                        UtilitiesMenuFragmentDirections
                            .actionUtilitiesMenuFragmentToDeadlineFragment()
                    )
                }
                R.id.nav_university_pass -> findNavController().safe {
                    navigate(
                        UtilitiesMenuFragmentDirections
                            .actionUtilitiesMenuFragmentToUniversityPassFragment()
                    )
                }
                R.id.nav_about_app -> findNavController().safe {
                    navigate(
                        UtilitiesMenuFragmentDirections
                            .actionUtilitiesMenuFragmentToAboutAppFragment()
                    )
                }
            }
        }
        viewBinding.listMenu.adapter = adapter
    }
}