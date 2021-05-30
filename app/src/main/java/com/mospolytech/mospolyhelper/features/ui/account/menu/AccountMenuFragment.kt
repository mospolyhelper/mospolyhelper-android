package com.mospolytech.mospolyhelper.features.ui.account.menu

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.view.menu.MenuBuilder
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.databinding.FragmentMenuAccountBinding
import com.mospolytech.mospolyhelper.utils.safe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class AccountMenuFragment : Fragment(R.layout.fragment_menu_account) {

    private lateinit var menuList: RecyclerView

    private val viewBinding by viewBinding(FragmentMenuAccountBinding::bind)
    private val viewModel by viewModel<MenuViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        GlobalScope.launch(Dispatchers.Main) {
//            viewModel.refresh().collect()
//        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val name = viewModel.getName()
        name?.let {
            viewBinding.textFio.text = it
            Glide.with(this).load(viewModel.getAvatar()).into(viewBinding.avatarUser)
        } ?: let {
            viewBinding.textFio.text = requireContext().getText(R.string.account)
        }
        viewBinding.avatarUser.isVisible = viewModel.getAvatar()?.isNotEmpty() == true
        menuList = viewBinding.listMenu

        setMenu(viewModel.getPermissions())
    }

    override fun onDestroyView() {
        Glide.with(this).clear(viewBinding.avatarUser)
        super.onDestroyView()
    }

    @SuppressLint("RestrictedApi")
    private fun setMenu(permissions: List<String>) {
        menuList.layoutManager = GridLayoutManager(context, 2)
        val menu = MenuBuilder(context)
        requireActivity().menuInflater.inflate(R.menu.menu_account, menu)
        val menuItems: MutableList<MenuItem> = mutableListOf()
        menuItems.add(menu.getItem(0))
        permissions.forEach {
            when (it) {
                "dialogs" -> { menuItems.add(menu.getItem(10)) }
                "info" -> { menuItems.add(menu.getItem(1)) }
                "payments" -> { menuItems.add(menu.getItem(4)) }
                "marks" -> { menuItems.add(menu.getItem(3)) }
                "grade-sheets" -> { menuItems.add(menu.getItem(9)) }
                "classmates" -> { menuItems.add(menu.getItem(7)) }
                "teachers" -> { menuItems.add(menu.getItem(6)) }
                "applications" -> { menuItems.add(menu.getItem(2)) }
                "myportfolio" -> { menuItems.add(menu.getItem(8)) }
                //"students" -> { menuItems.add(menu.getItem(5)) }
            }
        }
        menuItems.add(menu.getItem(5))
        val adapter = MenuAdapter(menuItems)
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
                            .actionAccountMenuFragmentToDeadlinesFragment())
                }
                R.id.nav_statements -> findNavController().safe {
                    navigate(
                        AccountMenuFragmentDirections
                            .actionAccountMenuFragmentToStatementsFragment()
                    )
                }
                R.id.nav_dialogs -> findNavController().safe {
                    navigate(
                        AccountMenuFragmentDirections
                            .actionAccountMenuFragmentToDialogsFragment()
                    )
                }
            }
        }
        menuList.adapter = adapter
    }
}