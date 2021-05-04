package com.mospolytech.mospolyhelper.features.ui.account.menu

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuItemImpl
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.utils.safe
import kotlinx.android.synthetic.main.fragment_menu_account.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class AccountMenuFragment : Fragment() {

    private lateinit var menuList: RecyclerView

    private lateinit var permissions: List<String>

    private val viewModel by viewModel<MenuViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        GlobalScope.launch(Dispatchers.Main) {
            viewModel.refresh().collect()
        }
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_menu_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val name = viewModel.getName()
        if (name.isNotEmpty()) {
            text_fio.text = name
        } else {
            text_fio.text = requireContext().getText(R.string.account)
        }
        text_fio.text = viewModel.getName()
        avatar_user.isVisible = viewModel.getAvatar().isNotEmpty()
        Glide.with(this).load(viewModel.getAvatar()).into(avatar_user)
        menuList = view.findViewById(R.id.listMenu)

        setMenu(viewModel.getPermissions())
    }

    @SuppressLint("RestrictedApi")
    private fun setMenu(permissions: List<String>) {
        menuList.layoutManager = GridLayoutManager(context, 3)
        val menu = MenuBuilder(context)
        requireActivity().menuInflater.inflate(R.menu.menu_account, menu)
//        val menuItems: MutableList<MenuItemImpl> = mutableListOf()
//        permissions.forEach {
//            when (it) {
//                //"dialogs" -> { menuItems.add(menu.visibleItems[1]) }
//                "info" -> { menuItems.add(menu.visibleItems[1]) }
//                "payments" -> { menuItems.add(menu.visibleItems[4]) }
//                "marks" -> { menuItems.add(menu.visibleItems[3]) }
//                "grade-sheets" -> { menuItems.add(menu.visibleItems[9]) }
//                "classmates" -> { menuItems.add(menu.visibleItems[7]) }
//                "teachers" -> { menuItems.add(menu.visibleItems[6]) }
//                "applications" -> { menuItems.add(menu.visibleItems[2]) }
//                "myportfolio" -> { menuItems.add(menu.visibleItems[8])}
//                "students" -> { menuItems.add(menu.visibleItems[5]) }
//            }
//        }
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
                            .actionAccountMenuFragmentToDeadlinesFragment())
                }
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