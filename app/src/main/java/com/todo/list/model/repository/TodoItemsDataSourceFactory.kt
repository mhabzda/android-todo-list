package com.todo.list.model.repository

import androidx.paging.DataSource
import com.todo.list.model.entities.TodoItem
import javax.inject.Inject

class TodoItemsDataSourceFactory @Inject constructor(
  private val todoItemsDataSource: TodoItemsDataSource
) : DataSource.Factory<Int, TodoItem>() {
  override fun create(): DataSource<Int, TodoItem> {
    return todoItemsDataSource
  }
}