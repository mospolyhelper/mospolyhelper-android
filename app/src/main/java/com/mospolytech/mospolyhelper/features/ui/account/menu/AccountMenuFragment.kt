package com.mospolytech.mospolyhelper.features.ui.account.menu

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.menu.MenuBuilder
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.utils.safe

class AccountMenuFragment : Fragment() {

    private lateinit var menuList: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_menu_account, container, false)
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
        requireActivity().menuInflater.inflate(R.menu.menu_account, menu)
        val adapter = MenuAdapter(menu)
        adapter.onItemMenuClick += {
            when (it) {
                R.id.nav_auth -> findNavController().safe {
                    navigate(
                        AccountMenuFragmentDirections
                            .actionAccountMenuFragmentToAuthFragment()
                    )
                }
                R.id.nav_students -> findNavController().safe {
                    navigate(
                        AccountMenuFragmentDirections
                            .actionAccountMenuFragmentToStudentsFragment()
                    )
                }
                R.id.nav_info -> findNavController().safe {
                    navigate(
                        AccountMenuFragmentDirections
                            .actionAccountMenuFragmentToInfoFragment()
                    )
                }
                R.id.nav_marks -> findNavController().safe {
                    navigate(
                        AccountMenuFragmentDirections
                            .actionAccountMenuFragmentToMarksFragment()
                    )
                }
                R.id.nav_teachers -> findNavController().safe {
                    navigate(
                        AccountMenuFragmentDirections
                            .actionAccountMenuFragmentToTeachersFragment()
                    )
                }
                R.id.nav_classmates -> findNavController().safe {
                    navigate(
                        AccountMenuFragmentDirections
                            .actionAccountMenuFragmentToClassmatesFragment()
                    )
                }
                R.id.nav_applications -> findNavController().safe {
                    navigate(
                        AccountMenuFragmentDirections
                            .actionAccountMenuFragmentToApplicationsFragment()
                    )
                }
                R.id.nav_payments -> findNavController().safe {
                    navigate(
                        AccountMenuFragmentDirections
                            .actionAccountMenuFragmentToPaymentsFragment()
                    )
                }
                R.id.nav_deadlines -> findNavController().safe {
                    navigate(
                        AccountMenuFragmentDirections
                            .actionAccountMenuFragmentToDeadlinesFragment()
                R.id.nav_statements -> findNavController().safe {
                    navigate(
                        AccountMenuFragmentDirections
                            .actionAccountMenuFragmentToStatementsFragment()
                    )
                }
            }
        }
        menuList.adapter = adapter
    }
}