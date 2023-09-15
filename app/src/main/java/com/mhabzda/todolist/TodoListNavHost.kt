package com.mhabzda.todolist

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.mhabzda.todolist.Destinations.ITEM_ROUTE
import com.mhabzda.todolist.Destinations.LIST_ROUTE
import com.mhabzda.todolist.ui.item.ItemScreen
import com.mhabzda.todolist.ui.list.ListScreen
import com.mhabzda.todolist.ui.list.ListViewModel

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
            val viewModel: ListViewModel = hiltViewModel()
            ListScreen(
                lazyPagingItems = viewModel.pagingEvents.collectAsLazyPagingItems(),
                navigateToAddItem = { navController.navigate(ITEM_ROUTE) },
                navigateToEditItem = { navController.navigate(ITEM_ROUTE) },
                deleteItem = { viewModel.deleteItem(it) }
            )
        }
        composable(ITEM_ROUTE) {
            ItemScreen(
                navigateBack = navController::navigateUp
            )
        }
    }
}