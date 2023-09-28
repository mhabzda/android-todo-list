package com.mhabzda.todolist.domain.usecase

import com.mhabzda.todolist.domain.model.TodoItem
import com.mhabzda.todolist.domain.repository.TodoItemRepository
import javax.inject.Inject

class GetTodoItemUseCase @Inject constructor(
    private val todoItemRepository: TodoItemRepository,
) {
    suspend fun invoke(id: String): Result<TodoItem> =
        todoItemRepository.getItem(id)
}
