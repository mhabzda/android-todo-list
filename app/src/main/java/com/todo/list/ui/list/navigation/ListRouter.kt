package com.todo.list.ui.list.navigation

import org.joda.time.DateTime

interface ListRouter {
    fun openItemCreationView()
    fun openItemEditionView(id: DateTime)
    fun openDeleteItemConfirmationDialog(deleteItemAction: () -> Unit)
}
