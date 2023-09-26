package com.mhabzda.todolist

import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mhabzda.todolist.Destination.ItemScreen
import com.mhabzda.todolist.Destination.ListScreen
import com.mhabzda.todolist.ui.item.ItemScreen
import com.mhabzda.todolist.ui.list.ListScreen

@Composable
fun TodoListNavHost(
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = ListScreen.name,
    ) {
        composable(route = ListScreen.name) {
            ListScreen(
                navigateToCreateItem = { navController.navigate(ItemScreen.getDirection()) },
                navigateToEditItem = { navController.navigate(ItemScreen.getDirection(it)) },
            )
        }
        composable(
            route = ItemScreen.name,
            arguments = ItemScreen.getNavArguments(),
        ) {
            ItemScreen(
                navigateBack = navController::navigateUp
            )
        }
    }
}

// TODO move to separate files
sealed class Destination {

    abstract val name: String
    abstract fun getDirection(vararg argument: Any?): String
    abstract fun getNavArguments(): List<NamedNavArgument>

    data object ListScreen : Destination() {
        override val name: String = "list"
        override fun getDirection(vararg argument: Any?) = name
        override fun getNavArguments(): List<NamedNavArgument> = emptyList()
    }

    data object ItemScreen : Destination() {

        const val ITEM_ID_ARG_NAME = "itemId"

        override val name: String = "item/?$ITEM_ID_ARG_NAME={$ITEM_ID_ARG_NAME}"

        override fun getDirection(vararg argument: Any?): String {
            val itemId = argument.firstOrNull() as? String
            return itemId?.let { name.replace("{$ITEM_ID_ARG_NAME}", it) } ?: name
        }

        override fun getNavArguments(): List<NamedNavArgument> =
            listOf(
                navArgument(ITEM_ID_ARG_NAME) {
                    type = NavType.StringType
                    nullable = true
                },
            )

    }
}