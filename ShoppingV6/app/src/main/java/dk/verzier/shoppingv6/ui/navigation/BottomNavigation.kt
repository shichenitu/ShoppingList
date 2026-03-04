package dk.verzier.shoppingv6.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Store
import androidx.compose.ui.graphics.vector.ImageVector
import dk.verzier.shoppingv6.R
import dk.verzier.shoppingv6.ui.features.settings.Settings
import dk.verzier.shoppingv6.ui.features.shoppinglist.ShoppingGraph
import dk.verzier.shoppingv6.ui.features.shops.Shops

interface AppRoute

interface NestedGraph : AppRoute {
    val startDestination: AppRoute
}

sealed class BottomNavigation(
    val route: AppRoute,
    val title: Int,
    val icon: ImageVector
) {
    data object ShoppingTab : BottomNavigation(
        route = ShoppingGraph,
        title = R.string.shopping_screen_title,
        icon = Icons.Default.ShoppingCart
    )

    data object ShopsTab : BottomNavigation(
        route = Shops,
        title = R.string.shops_screen_title,
        icon = Icons.Default.Store
    )

    data object SettingsTab : BottomNavigation(
        route = Settings,
        title = R.string.settings_screen_title,
        icon = Icons.Default.Settings
    )
}