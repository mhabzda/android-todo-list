package com.todo.list.model.repository

import androidx.paging.PagingData
import com.todo.list.model.entities.TodoItem
import io.reactivex.Completable
import io.reactivex.Observable
import kotlinx.coroutines.flow.Flow

interface TodoRepository {
    fun fetchTodoItems(pageSize: Int): Flow<PagingData<TodoItem>>
    fun observeItemsChanges(): Observable<Any>
    fun deleteItem(item: TodoItem): Completable
    fun saveItem(item: TodoItem): Completable
    fun editItem(item: TodoItem): Completable
}
