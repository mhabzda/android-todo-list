package com.todo.list.model

import io.reactivex.rxjava3.core.Single

interface TodoRepository {
  fun getTodoItems(): Single<List<TodoItem>>
}