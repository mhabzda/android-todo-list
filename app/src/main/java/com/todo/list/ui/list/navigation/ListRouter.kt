package com.todo.list.ui.list.navigation

interface ListRouter {
    fun openItemCreationView()
    fun openItemEditionView(id: String)
    fun openDeleteItemConfirmationDialog(deleteItemAction: () -> Unit)
}
