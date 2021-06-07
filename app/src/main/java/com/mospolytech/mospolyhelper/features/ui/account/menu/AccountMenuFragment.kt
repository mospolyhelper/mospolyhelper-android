package com.mospolytech.mospolyhelper.features.ui.account.menu

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.view.menu.MenuBuilder
import androidx.core.view.isVisible
import androidx.core.view.iterator
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.databinding.FragmentMenuAccountBinding
import com.mospolytech.mospolyhelper.utils.safe
import org.koin.androidx.viewmodel.ext.android.viewModel

class AccountMenuFragment : Fragment(R.layout.fragment_menu_account) {

    private lateinit var menuList: RecyclerView

    private val viewBinding by viewBinding(FragmentMenuAccountBinding::bind)
    private val viewModel by viewModel<MenuViewModel>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val name = viewModel.getName()
        name?.let {
            viewBinding.textFio.text = it
            Glide.with(this).load(viewModel.getAvatar()).circleCrop().into(viewBinding.avatarUser)
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

    private val idNavMap = mapOf(
        "dialogs" to R.id.nav_dialogs,
        "info" to R.id.nav_info,
        "payments" to R.id.nav_payments,
        "marks" to R.id.nav_marks,
        "grade-sheets" to R.id.nav_statements,
        "classmates" to R.id.nav_classmates,
        "teachers" to R.id.nav_teachers,
        "applications" to R.id.nav_applications,
//        "myportfolio" to R.id.nav_deadlines,
//        "students" to R.id.nav_students
    )

    private val idNavOrderMap = mapOf(
        "info" to 0,
        "payments" to 1,
        "applications" to 2,
        "marks" to 3,
        "grade-sheets" to 4,
        "dialogs" to 5,
        "classmates" to 6,
        "teachers" to 7,
        "students" to 8,
        "myportfolio" to 9,
    )

    private val idNavComparator = Comparator<String> { o1, o2 ->
        val i1 = idNavOrderMap[o1]
        val i2 = idNavOrderMap[o2]
        return@Comparator when {
            i1 == i2 ->  0
            i1 == null -> -1
            i2 == null -> 1
            else -> i1.compareTo(i2)
        }
    }



    @SuppressLint("RestrictedApi")
    private fun setMenu(permissions: List<String>) {
        menuList.layoutManager = GridLayoutManager(context, 2)
        val menu = MenuBuilder(context)
        requireActivity().menuInflater.inflate(R.menu.menu_account, menu)
        val menuItems: MutableList<MenuItem> = mutableListOf()
        val itemMap = menu.iterator().asSequence().associateBy { it.itemId }
        menuItems.add(itemMap[R.id.nav_auth]!!)
        permissions.sortedWith(idNavComparator).forEach { permission ->
            idNavMap[permission]?.let { menuItems.add(itemMap[it]!!) }
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