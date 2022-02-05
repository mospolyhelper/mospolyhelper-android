package com.mospolytech.mospolyhelper.features

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import com.mospolytech.features.base.core.navigation.getRoute
import com.mospolytech.features.base.core.navigation.rememberNavController
import com.mospolytech.features.base.navigation.MainScreen
import com.mospolytech.mospolyhelper.appScreens
import org.koin.androidx.compose.get
import org.koin.androidx.compose.getViewModel

val showNavBar = listOf(
    MainScreen.Home,
    MainScreen.Schedule,
    MainScreen.Account,
    MainScreen.Misc
).map { it.route }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContent(
    viewModel: MainViewModel = get()
) {
    val navController = rememberNavController(viewModel.router)

    Scaffold(
        bottomBar = { BottomNav(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = MainScreen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            appScreens()
        }
    }
}

@Composable
fun BottomNav(navController: NavHostController) {
    val items = listOf(
        MainScreen.Home,
        MainScreen.Schedule,
        MainScreen.Account,
        MainScreen.Misc
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    if (true || currentDestination?.route in showNavBar) {
        NavigationBar {
            items.forEach { screen ->
                val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                NavigationBarItem(
                    icon = { Icon(painterResource(screen.getIcon(selected)), contentDescription = null) },
                    selected = selected,
                    label = { Text(stringResource(screen.resourceId)) },
                    onClick = {
                        navController.navigate(screen.route) {
                            // Pop up to the start destination of the graph to
                            // avoid building up a large stack of destinations
                            // on the back stack as users select items
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}