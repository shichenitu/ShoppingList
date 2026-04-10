package dk.verzier.shoppingv10.ui.features.shoppinglist

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
    ) { backStackEntry -> // 1. Expose the backStackEntry here

        // 2. Grab the ViewModel explicitly so we can pass it down
        val viewModel: AddWhatViewModel = hiltViewModel()

        // 3. Observe the saved state handle result reactively
        val imageLabel by backStackEntry.savedStateHandle
            .getStateFlow<String?>(key = "image_label", initialValue = null)
            .collectAsStateWithLifecycle()

        // 4. When the label changes and isn't null, update the ViewModel
        LaunchedEffect(key1 = imageLabel) {
            imageLabel?.let {
                viewModel.uiEvents.onWhatChange(what = it)
                // Clear the state handle so it doesn't trigger again on rotation
                backStackEntry.savedStateHandle.remove<String>(key = "image_label")
            }
        }

        AddWhatScreen(
            viewModel = viewModel, // 5. Pass the ViewModel into the screen
            onNavigate = { event ->
                when (event) {
                    is AddWhatViewModel.NavigationEvent.NavigateUp -> navController.popBackStack()
                    is AddWhatViewModel.NavigationEvent.NavigateToAddWhere -> navController.navigate(
                        route = AddWhere(what = event.what, deadline = event.deadline),
                        navOptions = singleTopNavOptions
                    )
                    is AddWhatViewModel.NavigationEvent.NavigateToImageLabeling -> navController.navigate(
                        route = ImageLabeling,
                        navOptions = singleTopNavOptions
                    )
                }
            }
        )
    }
    composable<ImageLabeling> {
        ImageLabelingScreen(
            onNavigate = { event ->
                when (event) {
                    is ImageLabelingViewModel.NavigationEvent.NavigateUp -> navController.popBackStack()
                    is ImageLabelingViewModel.NavigationEvent.NavigateUpWithResult -> {
                        // 6. Set the result in the previous screen's saved state handle
                        navController.previousBackStackEntry?.savedStateHandle?.set("image_label", event.label)
                        navController.popBackStack()
                    }
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