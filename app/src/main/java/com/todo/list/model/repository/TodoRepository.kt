package com.todo.list.model.repository

import androidx.paging.PagedList
import com.todo.list.model.entities.TodoItem
import io.reactivex.Observable

interface TodoRepository {
  fun fetchTodoItems(): Observable<PagedList<TodoItem>>
  fun refreshTodoItems()
}