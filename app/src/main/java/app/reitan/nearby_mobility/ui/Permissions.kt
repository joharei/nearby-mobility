@file:Suppress("unused")

package app.reitan.nearby_mobility.ui

import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.*
import androidx.compose.ui.platform.ContextAmbient

@Composable
fun <I, O> ActivityResultRegistry.activityResultLauncher(
    requestContract: ActivityResultContract<I, O>,
    onResult: (O) -> Unit
): ActivityResultLauncher<I> {
    val key = currentComposer.currentCompoundKeyHash.toString()
    val launcher = remember(requestContract, onResult) {
        register(key, requestContract, onResult)
    }
    onDispose {
        launcher.unregister()
    }
    return launcher
}

class PermissionState(
    val permission: String,
    hasPermissionState: State<Boolean>,
    shouldShowRationaleState: State<Boolean>,
    private val launcher: ActivityResultLauncher<String>?
) {
    val hasPermission by hasPermissionState
    val shouldShowRationale by shouldShowRationaleState

    fun launchPermissionRequest() = launcher?.launch(permission)
}

private fun Context.findActivity(): AppCompatActivity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is AppCompatActivity) return context
        context = context.baseContext
    }
    return null
}

@Composable
fun permissionState(
    permission: String
): PermissionState {
    val context = ContextAmbient.current
    val activity = context.findActivity()
    val permissionState = mutableStateOf(
        context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    )
    val rationaleState = mutableStateOf(
        activity?.shouldShowRequestPermissionRationale(permission) == true
    )
    val launcher =
        activity?.activityResultRegistry?.activityResultLauncher(ActivityResultContracts.RequestPermission()) {
            permissionState.value = it
            rationaleState.value = activity.shouldShowRequestPermissionRationale(permission) == true
        }
    return remember(launcher) {
        PermissionState(permission, permissionState, rationaleState, launcher)
    }
}