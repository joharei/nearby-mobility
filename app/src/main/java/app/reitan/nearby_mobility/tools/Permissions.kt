@file:Suppress("unused")

package app.reitan.nearby_mobility.tools

import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityOptionsCompat
import java.util.*

@Composable
fun <I, O> registerForActivityResult(
    contract: ActivityResultContract<I, O>,
    onResult: (O) -> Unit,
): ActivityResultLauncher<I> {
    // First, find the ActivityResultRegistry by casting the Context
    // (which is actually a ComponentActivity) to ActivityResultRegistryOwner
    val owner = LocalContext.current as ActivityResultRegistryOwner
    val activityResultRegistry = owner.activityResultRegistry

    // Keep track of the current onResult listener
    val currentOnResult = rememberUpdatedState(onResult)

    // It doesn't really matter what the key is, just that it is unique
    // and consistent across configuration changes
    val key = rememberSaveable { UUID.randomUUID().toString() }

    // Since we don't have a reference to the real ActivityResultLauncher
    // until we register(), we build a layer of indirection so we can
    // immediately return an ActivityResultLauncher
    // (this is the same approach that Fragment.registerForActivityResult uses)
    val realLauncher = mutableStateOf<ActivityResultLauncher<I>?>(null)
    val returnedLauncher = remember {
        object : ActivityResultLauncher<I>() {
            override fun launch(input: I, options: ActivityOptionsCompat?) {
                realLauncher.value?.launch(input, options)
            }

            override fun unregister() {
                realLauncher.value?.unregister()
            }

            override fun getContract() = contract
        }
    }

    // DisposableEffect ensures that we only register once
    // and that we unregister when the composable is disposed
    DisposableEffect(activityResultRegistry, key, contract) {
        realLauncher.value = activityResultRegistry.register(key, contract) {
            currentOnResult.value(it)
        }
        onDispose {
            realLauncher.value?.unregister()
        }
    }
    return returnedLauncher
}

class PermissionState(
    private val permission: String,
    hasPermissionState: State<Boolean>,
    shouldShowRationaleState: State<Boolean>,
    private val launcher: ActivityResultLauncher<String>?,
) {
    val hasPermission by hasPermissionState
    val shouldShowRationale by shouldShowRationaleState

    fun launchPermissionRequest() = launcher?.launch(permission)
}

private fun Context.findActivity(): ComponentActivity? {
    var context = this
    do {
        if (context is ComponentActivity) return context
        if (context is ContextWrapper) context = context.baseContext
    } while (context is ContextWrapper)
    return null
}

@Composable
fun permissionState(
    permission: String,
): PermissionState {
    val context = LocalContext.current
    val activity = context.findActivity()
    val permissionState = mutableStateOf(
        context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    )
    val rationaleState = mutableStateOf(
        activity?.shouldShowRequestPermissionRationale(permission) == true
    )
    val launcher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            permissionState.value = it
            rationaleState.value =
                activity?.shouldShowRequestPermissionRationale(permission) == true
        }
    return remember(launcher) {
        PermissionState(permission, permissionState, rationaleState, launcher)
    }
}
