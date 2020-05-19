package com.todo.list.ui.list

import androidx.paging.PagedList
import com.todo.list.model.entities.TodoItem

interface ListContract {
  interface View {
    fun displayTodoList(items: PagedList<TodoItem>)
    fun setRefreshingState(isRefreshing: Boolean)
    fun displayError(errorMessage: String)
  }

  interface Presenter {
    fun observePagedData()
    fun refreshItems()
    fun floatingButtonClicked()
    fun itemLongClicked(item: TodoItem)
    fun clearResources()
  }

  interface Router {
    fun openItemCreationView()
    fun openDeleteItemConfirmationDialog(deleteItemAction: () -> Unit)
  }
}