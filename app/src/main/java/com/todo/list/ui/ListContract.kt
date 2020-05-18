package com.todo.list.ui

import androidx.paging.PagedList
import com.todo.list.model.entities.TodoItem

interface ListContract {
  interface View {
    fun displayTodoList(items: PagedList<TodoItem>)
  }

  interface Presenter {
    fun fetchItems()
    fun refreshItems()
  }
}