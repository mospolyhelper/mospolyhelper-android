package com.mospolytech.mospolyhelper.features.ui.utilities.menu

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.menu.MenuBuilder
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.utils.safe

class UtilitiesMenuFragment : Fragment() {

    private lateinit var menuList: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_menu_utilities, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        menuList = view.findViewById(R.id.listMenu)

        setMenu()
    }

    @SuppressLint("RestrictedApi")
    private fun setMenu() {
        menuList.layoutManager = GridLayoutManager(context, 3)
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
        menuList.adapter = adapter
    }
}