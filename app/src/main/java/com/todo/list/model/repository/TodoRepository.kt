package com.todo.list.model.repository

import androidx.paging.PagingData
import com.todo.list.model.entities.TodoItem
import kotlinx.coroutines.flow.Flow

interface TodoRepository {
    fun fetchTodoItems(pageSize: Int): Flow<PagingData<TodoItem>>
    fun observeItemsChanges(): Flow<Unit>
    suspend fun deleteItem(item: TodoItem): Result<Unit>
    suspend fun saveItem(item: TodoItem): Result<Unit>
    suspend fun editItem(item: TodoItem): Result<Unit>
}
