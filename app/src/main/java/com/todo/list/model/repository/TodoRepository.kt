package com.todo.list.model.repository

import com.todo.list.model.entities.TodoItem
import io.reactivex.Single

interface TodoRepository {
  fun getTodoItems(): Single<List<TodoItem>>
}