package com.todo.list.model.repository

import androidx.paging.PagingData
import com.todo.list.model.entities.TodoItem
import kotlinx.coroutines.flow.Flow
import org.joda.time.DateTime

interface TodoRepository {
    fun fetchTodoItems(pageSize: Int): Flow<PagingData<TodoItem>>
    fun observeItemsChanges(): Flow<Unit>
    suspend fun getItem(id: DateTime): Result<TodoItem>
    suspend fun deleteItem(id: DateTime): Result<Unit>
    suspend fun saveItem(item: TodoItem): Result<Unit>
    suspend fun editItem(item: TodoItem): Result<Unit>
}
