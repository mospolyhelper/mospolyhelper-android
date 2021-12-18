package com.mospolytech.domain.base.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted

///**
// * Launches a new coroutine and repeats `block` every time the Fragment's viewLifecycleOwner
// * is in and out of `minActiveState` lifecycle state.
// */
//inline fun Fragment.launchInViewLifecycle(
//    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
//    crossinline block: suspend CoroutineScope.() -> Unit
//) {
//    viewLifecycleOwner.lifecycleScope.launch {
//        viewLifecycleOwner.lifecycle.repeatOnLifecycle(minActiveState) {
//            block()
//        }
//    }
//}


private const val StopTimeoutMillis: Long = 5000

/**
 * A [SharingStarted] meant to be used with a [StateFlow] to expose data to a view.
 *
 * When the view stops observing, upstream flows stay active for some time to allow the system to
 * come back from a short-lived configuration change (such as rotations). If the view stops
 * observing for longer, the cache is kept but the upstream flows are stopped. When the view comes
 * back, the latest value is replayed and the upstream flows are executed again. This is done to
 * save resources when the app is in the background but let users switch between apps quickly.
 */
val WhileViewSubscribed: SharingStarted = SharingStarted.WhileSubscribed(StopTimeoutMillis)

