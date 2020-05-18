package com.todo.list.model.repository

import com.todo.list.model.repository.model.PagingObservable
import io.reactivex.Observable

interface TodoRepository {
  fun fetchTodoItems(): PagingObservable
  fun refreshTodoItems()
  fun observeItemsChanges(): Observable<Any>
}