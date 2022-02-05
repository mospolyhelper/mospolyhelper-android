package com.mospolytech.features.base.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.Navigator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ComposeNavigator(
    val navController: NavHostController,
    private val router: Router
) {
    private val scope = CoroutineScope(Job() + Dispatchers.Main)

    init {
        scope.launch {
            router.commandBuffer.collect {
                processCommand(it)
            }
        }
    }

    private fun processCommand(commands: List<Command>) {
        for (command in commands) {
            applyCommand(command)
        }
    }

    private fun applyCommand(command: Command) {
        when (command) {
            is Command.Forward -> forward(command)
            is Command.Replace -> replace(command)
            is Command.BackTo -> backTo(command)
            is Command.Back -> back()
        }
    }

    private fun forward(command: Command.Forward) {
        navController.navigate(buildFullRoute(command.screen, command.key))
    }

    private fun replace(command: Command.Replace) {
        navController.popBackStack()
        navController.navigate(buildFullRoute(command.screen, command.key))
    }

    private fun back() {
        navController.navigateUp()
    }

    private fun backTo(command: Command.BackTo) {
        if (command.screen == null) {
            navController.backQueue.firstOrNull()?.let {
                navController.popBackStack(it.id, false)
            }
        } else {
            navController.popBackStack(buildFullRoute(command.screen, command.key ?: ""), true)
        }
    }

    private fun buildFullRoute(screen: BaseScreen, key: String): String {
        return "$key?screen=${screen.toUrlParameter()}"
    }
}

@Composable
fun rememberNavController(
    router: Router,
    vararg navigators: Navigator<out NavDestination>
): NavHostController {
    val navController = androidx.navigation.compose.rememberNavController(*navigators)
    val navigator = remember(navController, router) {
        ComposeNavigator(navController, router)
    }

    return navigator.navController
}
