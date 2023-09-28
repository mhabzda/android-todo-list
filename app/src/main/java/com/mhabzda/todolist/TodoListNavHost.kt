package com.mhabzda.todolist

import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.mhabzda.todolist.item.ItemScreenDestination
import com.mhabzda.todolist.list.ListScreenDestination

const val NAVIGATION_ANIMATION_DURATION_MILLIS = 400

@Composable
fun TodoListNavHost(
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = ListScreenDestination.ROUTE,
    ) {
        ListScreenDestination.define(navGraphBuilder = this, navController = navController)
        ItemScreenDestination.define(navGraphBuilder = this, navController = navController)
    }
}

abstract class Destination {
    abstract fun getNavigationRoute(vararg argument: Any?): String
    abstract fun getNavArguments(): List<NamedNavArgument>
    abstract fun define(navGraphBuilder: NavGraphBuilder, navController: NavController)
}
