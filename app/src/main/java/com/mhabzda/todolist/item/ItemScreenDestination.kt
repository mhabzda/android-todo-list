package com.mhabzda.todolist.item

import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Companion.Left
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Companion.Right
import androidx.compose.animation.core.tween
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.mhabzda.todolist.Destination
import com.mhabzda.todolist.NAVIGATION_ANIMATION_DURATION_MILLIS

data object ItemScreenDestination : Destination() {

    const val ITEM_ID_ARG_NAME = "itemId"

    private const val route: String = "item/?$ITEM_ID_ARG_NAME={$ITEM_ID_ARG_NAME}"

    override fun getNavigationRoute(vararg argument: Any?): String {
        val itemId = argument.firstOrNull() as? String
        return itemId?.let { route.replace("{$ITEM_ID_ARG_NAME}", it) } ?: route
    }

    override fun getNavArguments(): List<NamedNavArgument> =
        listOf(
            navArgument(ITEM_ID_ARG_NAME) {
                type = NavType.StringType
                nullable = true
            },
        )

    override fun define(navGraphBuilder: NavGraphBuilder, navController: NavController) = with(navGraphBuilder) {
        composable(
            route = this@ItemScreenDestination.route,
            arguments = getNavArguments(),
            enterTransition = { slideIntoContainer(towards = Left, animationSpec = tween(NAVIGATION_ANIMATION_DURATION_MILLIS)) },
            exitTransition = { slideOutOfContainer(towards = Right, animationSpec = tween(NAVIGATION_ANIMATION_DURATION_MILLIS)) },
        ) {
            ItemScreen(
                navigateBack = navController::navigateUp,
            )
        }
    }
}