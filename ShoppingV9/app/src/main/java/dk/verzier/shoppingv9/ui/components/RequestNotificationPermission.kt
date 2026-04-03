package dk.verzier.shoppingv9.ui.components

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

@Composable
fun RequestNotificationPermission() {
    // We only need to ask on Android 13 (TIRAMISU) and above
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

        // 1. Create the launcher that handles the result
        val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { isGranted ->
                if (isGranted) {
                    // Yay! The OS will now let your WorkManager show notifications.
                } else {
                    // The user denied it. You could optionally show a Snackbar here
                    // explaining why the app needs notifications.
                }
            }
        )

        // 2. Launch the request exactly once when this Composable enters the composition
        LaunchedEffect(key1 = Unit) {
            permissionLauncher.launch(input = Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}