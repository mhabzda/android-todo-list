package com.mhabzda.todolist

import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Companion.Left
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Companion.Right
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mhabzda.todolist.item.ItemScreen
import com.mhabzda.todolist.item.ItemScreenDestination
import com.mhabzda.todolist.list.ListScreen
import com.mhabzda.todolist.list.ListScreenDestination

private const val NAVIGATION_ANIMATION_DURATION_MILLIS = 400

@Composable
fun TodoListNavHost(
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = ListScreenDestination.name,
    ) {
        composable(
            route = ListScreenDestination.name,
            exitTransition = { slideOutOfContainer(towards = Left, animationSpec = tween(NAVIGATION_ANIMATION_DURATION_MILLIS)) },
            popEnterTransition = { slideIntoContainer(towards = Right, animationSpec = tween(NAVIGATION_ANIMATION_DURATION_MILLIS)) },
        ) {
            ListScreen(
                navigateToCreateItem = { navController.navigate(ItemScreenDestination.getDirection()) },
                navigateToEditItem = { navController.navigate(ItemScreenDestination.getDirection(it)) },
            )
        }
        composable(
            route = ItemScreenDestination.name,
            arguments = ItemScreenDestination.getNavArguments(),
            enterTransition = { slideIntoContainer(towards = Left, animationSpec = tween(NAVIGATION_ANIMATION_DURATION_MILLIS)) },
            exitTransition = { slideOutOfContainer(towards = Right, animationSpec = tween(NAVIGATION_ANIMATION_DURATION_MILLIS)) },
        ) {
            ItemScreen(
                navigateBack = navController::navigateUp
            )
        }
    }
}

abstract class Destination {

    abstract val name: String
    abstract fun getDirection(vararg argument: Any?): String
    abstract fun getNavArguments(): List<NamedNavArgument>
}