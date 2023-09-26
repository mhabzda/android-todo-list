package com.mhabzda.todolist.list

import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Companion.Left
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Companion.Right
import androidx.compose.animation.core.tween
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.mhabzda.todolist.Destination
import com.mhabzda.todolist.NAVIGATION_ANIMATION_DURATION_MILLIS
import com.mhabzda.todolist.item.ItemScreenDestination

data object ListScreenDestination : Destination() {

    const val ROUTE: String = "list"

    override fun getNavigationRoute(vararg argument: Any?) = ROUTE

    override fun getNavArguments(): List<NamedNavArgument> = emptyList()

    override fun define(navGraphBuilder: NavGraphBuilder, navController: NavController) = with(navGraphBuilder) {
        composable(
            route = this@ListScreenDestination.ROUTE,
            exitTransition = { slideOutOfContainer(towards = Left, animationSpec = tween(NAVIGATION_ANIMATION_DURATION_MILLIS)) },
            popEnterTransition = { slideIntoContainer(towards = Right, animationSpec = tween(NAVIGATION_ANIMATION_DURATION_MILLIS)) },
        ) {
            ListScreen(
                navigateToCreateItem = { navController.navigate(ItemScreenDestination.getNavigationRoute()) },
                navigateToEditItem = { navController.navigate(ItemScreenDestination.getNavigationRoute(it)) },
            )
        }
    }
}