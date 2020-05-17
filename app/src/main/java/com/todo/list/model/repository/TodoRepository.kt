package com.todo.list.model.repository

import androidx.paging.PagedList
import com.todo.list.model.entities.TodoItem
import io.reactivex.Observable
import io.reactivex.Single

interface TodoRepository {
  fun getTodoItems(): Single<List<TodoItem>>

  fun fetchTodoItems(): Observable<PagedList<TodoItem>>
}