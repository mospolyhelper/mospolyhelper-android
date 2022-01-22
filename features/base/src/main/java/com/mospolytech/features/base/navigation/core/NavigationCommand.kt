package com.mospolytech.features.base.navigation.core


/**
 * Navigation command describes screens transition.
 */
sealed interface Command {

    /**
     * Opens new screen.
     */
    data class Forward(val screen: Screen) : Command

    /**
     * Replaces the current screen.
     */
    data class Replace(val screen: Screen) : Command

    /**
     * Rolls fragmentBack the last transition from the screens chain.
     */
    object Back : Command

    /**
     * Rolls fragmentBack to the needed screen from the screens chain.
     *
     * Behavior in the case when no needed screens found depends on an implementation of the
     * But the recommended behavior is to return to the root.
     */
    data class BackTo(val screen: Screen?) : Command
}
