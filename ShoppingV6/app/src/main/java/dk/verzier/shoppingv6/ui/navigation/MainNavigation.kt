package dk.verzier.shoppingv6.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dk.verzier.shoppingv6.ui.features.settings.Settings
import dk.verzier.shoppingv6.ui.features.settings.SettingsScreen
import dk.verzier.shoppingv6.ui.features.shoppinglist.shoppingNavGraph
import dk.verzier.shoppingv6.ui.features.shops.Shops
import dk.verzier.shoppingv6.ui.features.shops.ShopsScreen

@Composable
fun MainNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val destinations = listOf(
        BottomNavigation.ShoppingTab,
        BottomNavigation.ShopsTab,
        BottomNavigation.SettingsTab
    )

    Scaffold(
        modifier = modifier,
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                destinations.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(imageVector = screen.icon, contentDescription = null) },
                        label = { Text(text = stringResource(id = screen.title)) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route::class.qualifiedName } == true,
                        onClick = {
                            navController.navigate(screen.route) {
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
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavigation.ShoppingTab.route,
            modifier = Modifier.padding(paddingValues = innerPadding)
        ) {
            shoppingNavGraph(navController = navController)
            composable<Shops> {
                ShopsScreen()
            }
            composable<Settings> {
                SettingsScreen()
            }
        }
    }
}