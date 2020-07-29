package com.mospolytech.mospolyhelper.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.navigation.NavigationView
import com.mospolytech.mospolyhelper.R
import org.koin.core.KoinComponent
import org.koin.core.inject

class MainMenuFragment : BottomSheetDialogFragment(), KoinComponent {

    private val viewModel by inject<MainViewModel>()

    private lateinit var navigationView: NavigationView
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_main_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navigationView = view.findViewById(R.id.nav_view)
        navController = findNavController()
        if (viewModel.currentFragmentNavId != -1) {
            navigationView.setCheckedItem(viewModel.currentFragmentNavId)
        }
        navigationView.setNavigationItemSelectedListener {
            val action = navController.graph.getAction(it.itemId)
            val currentDestination = navController.currentBackStackEntry?.destination
            if (action != null && currentDestination!= null && action.destinationId != currentDestination.id) {
                viewModel.currentFragmentNavId = it.itemId
                navController.navigate(it.itemId)
            }
            true
        }
    }
}