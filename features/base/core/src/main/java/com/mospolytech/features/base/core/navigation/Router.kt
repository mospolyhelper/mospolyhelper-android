package com.mospolytech.features.base.core.navigation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class Router {
    private val _commandBuffer = MutableSharedFlow<List<Command>>()
    val commandBuffer: Flow<List<Command>> = _commandBuffer

    private val scope = CoroutineScope(Job() + Dispatchers.Default)

    fun executeCommands(vararg commands: Command) {
        scope.launch {
            _commandBuffer.emit(commands.toList())
        }
    }

    /**
     * Open new screen and add it to the screens chain.
     *
     * @param screen screen
     */
    fun navigateTo(screen: BaseScreen, key: String = "") {
        executeCommands(Command.Forward(screen, key))
    }

    /**
     * Clear all screens and open new one as root.
     *
     * @param screen screen
     */
    fun newRootScreen(screen: BaseScreen, key: String = "") {
        executeCommands(Command.BackTo(null, key), Command.Replace(screen))
    }

    /**
     * Replace current screen.
     *
     * By replacing the screen, you alters the backstack,
     * so by going fragmentBack you will return to the previous screen
     * and not to the replaced one.
     *
     * @param screen screen
     */
    fun replaceScreen(screen: BaseScreen, key: String = "") {
        executeCommands(Command.Replace(screen, key))
    }

    /**
     * Return fragmentBack to the needed screen from the chain.
     *
     * Behavior in the case when no needed screens found depends on
     * the processing of the [Command.BackTo] command in a [Navigator] implementation.
     *
     * @param screen screen
     */
    fun backTo(screen: BaseScreen?, key: String = "") {
        executeCommands(Command.BackTo(screen, key))
    }

    /**
     * Opens several screens inside single transaction.
     *
     * @param screens
     */
    fun newChain(vararg screens: BaseScreen, key: String = "") {
        val commands = screens.map { Command.Forward(it) }
        executeCommands(*commands.toTypedArray())
    }

    /**
     * Clear current stack and open several screens inside single transaction.
     *
     * @param screens
     */
    fun newRootChain(vararg screens: BaseScreen, key: String = "") {
        val commands = screens.mapIndexed { index, screen ->
            if (index == 0)
                Command.Replace(screen)
            else
                Command.Forward(screen)
        }
        executeCommands(Command.BackTo(null), *commands.toTypedArray())
    }

    /**
     * Remove all screens from the chain and exit.
     *
     * It's mostly used to finish the application or close a supplementary navigation chain.
     */
    fun finishChain() {
        executeCommands(Command.BackTo(null), Command.Back)
    }

    /**
     * Return to the previous screen in the chain.
     *
     * Behavior in the case when the current screen is the root depends on
     * the processing of the [Command.Back] command in a [Navigator] implementation.
     */
    fun exit() {
        executeCommands(Command.Back)
    }



    inline fun <reified T : BaseScreen> navigateTo(screen: T) {
        navigateTo(screen, getRoute<T>())
    }
    inline fun <reified T : BaseScreen> newRootScreen(screen: T) {
        newRootScreen(screen, getRoute<T>())
    }
    inline fun <reified T : BaseScreen> replaceScreen(screen: T) {
        replaceScreen(screen, getRoute<T>())
    }
    inline fun <reified T : BaseScreen> backTo(screen: T?) {
        backTo(screen, getRoute<T>())
    }
//    inline fun <reified T : BaseScreen> newChain(screen: T) {
//        newChain(screen, T::class.qualifiedName ?: "")
//    }
//    inline fun <reified T: BaseScreen> newRootChain(vararg screens: T) {
//        newRootChain(T::class.qualifiedName ?: "", *screens)
//    }
}