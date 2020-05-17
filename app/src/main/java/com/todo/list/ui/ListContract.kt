package com.todo.list.ui

import com.todo.list.model.entities.TodoItem

interface ListContract {
  interface View {
    fun displayTodoList(items: List<TodoItem>)
  }

  interface Presenter {
    fun fetchItems()
  }
}