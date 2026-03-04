package dk.verzier.shoppingv6

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import dk.verzier.shoppingv6.domain.Theme
import dk.verzier.shoppingv6.ui.features.settings.SettingsViewModel
import dk.verzier.shoppingv6.ui.navigation.MainNavigation
import dk.verzier.shoppingv6.ui.theme.ShoppingV6Theme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: SettingsViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val darkTheme = when (uiState.theme) {
                Theme.LIGHT -> false
                Theme.DARK -> true
                Theme.SYSTEM -> isSystemInDarkTheme()
            }

            // We call enableEdgeToEdge here, inside setContent, to recompose when the theme changes.
            // This ensures the status and navigation bar colors update dynamically.
            // The SystemBarStyle.auto() constructor requires us to explicitly provide the scrim
            // colors. We are using the library's default values to maintain standard behavior.
            enableEdgeToEdge(
                statusBarStyle = androidx.activity.SystemBarStyle.auto(
                    lightScrim = android.graphics.Color.TRANSPARENT,
                    darkScrim = android.graphics.Color.TRANSPARENT,
                ) { darkTheme },
                navigationBarStyle = androidx.activity.SystemBarStyle.auto(
                    lightScrim = android.graphics.Color.argb(
                        /* alpha = */0xe6,
                        /* red = */ 0xFF,
                        /* green = */ 0xFF,
                        /* blue = */ 0xFF
                    ),
                    darkScrim = android.graphics.Color.argb(
                        /* alpha = */ 0x80,
                        /* red = */ 0x1b,
                        /* green = */ 0x1b,
                        /* blue = */ 0x1b
                    ),
                ) { darkTheme },
            )
            ShoppingV6Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainNavigation()
                }
            }
        }
    }
}