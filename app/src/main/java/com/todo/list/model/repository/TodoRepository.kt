package com.todo.list.model.repository

import com.todo.list.model.entities.TodoItem
import com.todo.list.model.repository.model.PagingObservable
import io.reactivex.Observable

interface TodoRepository {
  fun fetchTodoItems(pageSize: Int): PagingObservable
  fun refreshTodoItems()
  fun observeItemsChanges(): Observable<Any>
  fun deleteItem(item: TodoItem)
}