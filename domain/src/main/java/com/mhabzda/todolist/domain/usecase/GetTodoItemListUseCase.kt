package com.mhabzda.todolist.domain.usecase

import com.mhabzda.todolist.domain.model.TodoItem
import com.mhabzda.todolist.domain.repository.TodoItemRepository
import javax.inject.Inject

class GetTodoItemListUseCase @Inject constructor(
    private val todoItemRepository: TodoItemRepository,
) {
    suspend fun invoke(pageSize: Int, itemIdFrom: String?): Result<List<TodoItem>> =
        todoItemRepository.getItems(pageSize = pageSize, itemIdFrom = itemIdFrom)
}