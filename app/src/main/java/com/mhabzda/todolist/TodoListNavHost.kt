package com.mhabzda.todolist

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mhabzda.todolist.Destinations.ITEM_ROUTE
import com.mhabzda.todolist.Destinations.LIST_ROUTE
import com.mhabzda.todolist.ui.item.ItemScreen
import com.mhabzda.todolist.ui.list.ListScreen

object Destinations {
    const val LIST_ROUTE = "list"
    const val ITEM_ROUTE = "item"
}

@Composable
fun TodoListNavHost(
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = LIST_ROUTE,
    ) {
        composable(LIST_ROUTE) {
            ListScreen(
                navigateToAddItem = { navController.navigate(ITEM_ROUTE) },
                navigateToEditItem = { navController.navigate(ITEM_ROUTE) },
            )
        }
        composable(ITEM_ROUTE) {
            ItemScreen(
                navigateBack = navController::navigateUp
            )
        }
    }
}