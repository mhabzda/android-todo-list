package com.mhabzda.todolist.domain.repository

import com.mhabzda.todolist.domain.model.TodoItem

interface TodoItemRepository {
    suspend fun getItems(pageSize: Int, itemIdFrom: String? = null): Result<List<TodoItem>>
    suspend fun getItem(id: String): Result<TodoItem>
    suspend fun deleteItem(id: String): Result<Unit>
    suspend fun saveItem(title: String, description: String, iconUrl: String?): Result<Unit>
    suspend fun editItem(id: String, title: String, description: String, iconUrl: String?): Result<Unit>
}