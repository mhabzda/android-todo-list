package com.mhabzda.todolist.domain.usecase

import com.mhabzda.todolist.domain.repository.TodoItemRepository
import javax.inject.Inject

class DeleteTodoItemUseCase @Inject constructor(
    private val todoItemRepository: TodoItemRepository,
) {
    suspend fun invoke(id: String): Result<Unit> =
        todoItemRepository.deleteItem(id)
}
