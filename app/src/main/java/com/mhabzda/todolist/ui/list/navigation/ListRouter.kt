package com.mhabzda.todolist.ui.list.navigation

interface ListRouter {
    fun openItemCreationView()
    fun openItemEditionView(id: String)
    fun openDeleteItemConfirmationDialog(deleteItemAction: () -> Unit)
}
