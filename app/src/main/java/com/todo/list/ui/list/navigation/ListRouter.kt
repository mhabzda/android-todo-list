package com.todo.list.ui.list.navigation

import com.todo.list.ui.parcel.TodoItemParcelable

interface ListRouter {
    fun openItemCreationView()
    fun openItemEditionView(item: TodoItemParcelable)
    fun openDeleteItemConfirmationDialog(deleteItemAction: () -> Unit)
}
