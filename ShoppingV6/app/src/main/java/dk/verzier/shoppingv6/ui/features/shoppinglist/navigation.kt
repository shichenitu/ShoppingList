package dk.verzier.shoppingv6.ui.features.shoppinglist

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.navigation
import androidx.navigation.navDeepLink
import androidx.navigation.navOptions

fun NavGraphBuilder.shoppingNavGraph(navController: NavHostController) {

    val singleTopNavOptions: NavOptions = navOptions {
        launchSingleTop = true
    }

    navigation<ShoppingGraph>(startDestination = ShoppingList()) {
        composable<ShoppingList>(
            deepLinks = listOf(
                navDeepLink { uriPattern = "shopping://items/{itemId}" }
            )
        ) {
            ShoppingListScreen(
                onNavigate = { event ->
                    when (event) {
                        is ShoppingListViewModel.NavigationEvent.NavigateToAddWhat -> navController.navigate(
                            route = AddWhat,
                            navOptions = singleTopNavOptions
                        )
                    }
                }
            )
        }
    }
    composable<AddWhat>(
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(durationMillis = 500)
            )
        },
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(durationMillis = 500)
            )
        },
        popEnterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(durationMillis = 500)
            )
        },
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(durationMillis = 500)
            )
        }
    ) {
        AddWhatScreen(
            onNavigate = { event ->
                when (event) {
                    is AddWhatViewModel.NavigationEvent.NavigateUp -> navController.popBackStack()
                    is AddWhatViewModel.NavigationEvent.NavigateToAddWhere -> navController.navigate(
                        route = AddWhere(what = event.what, deadline = event.deadline),
                        navOptions = singleTopNavOptions
                    )
                }
            }
        )
    }
    dialog<AddWhere> {
        AddWhereScreen(
            onNavigate = { event ->
                when (event) {
                    is AddWhereViewModel.NavigationEvent.CloseDialog -> navController.popBackStack()
                    is AddWhereViewModel.NavigationEvent.NavigateToShoppingList -> navController.navigate(
                        route = ShoppingList(),
                        navOptions = navOptions {
                            popUpTo(route = ShoppingGraph)
                        }
                    )
                }
            }
        )
    }
}