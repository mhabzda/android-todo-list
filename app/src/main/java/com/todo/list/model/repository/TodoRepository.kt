package com.todo.list.model.repository

import com.todo.list.model.repository.model.PagingObservable

interface TodoRepository {
  fun fetchTodoItems(): PagingObservable
  fun refreshTodoItems()
}