package com.todo.list.ui.list

import androidx.paging.PagedList
import com.todo.list.model.entities.TodoItem
import com.todo.list.ui.parcel.TodoItemParcelable

interface ListContract {
    interface View {
        fun displayTodoList(items: PagedList<TodoItem>)
        fun setRefreshingState(isRefreshing: Boolean)
        fun displayError(errorMessage: String)
        fun displayItemDeletionConfirmationMessage()
    }

    interface Presenter {
        fun observePagedData()
        fun refreshItems()
        fun floatingButtonClicked()
        fun itemLongClicked(item: TodoItem)
        fun itemClicked(item: TodoItem)
        fun clearResources()
    }

    interface Router {
        fun openItemCreationView()
        fun openItemEditionView(item: TodoItemParcelable)
        fun openDeleteItemConfirmationDialog(deleteItemAction: () -> Unit)
    }
}
