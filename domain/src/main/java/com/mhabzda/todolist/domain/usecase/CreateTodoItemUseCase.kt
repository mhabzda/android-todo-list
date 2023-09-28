package com.mhabzda.todolist.domain.usecase

import com.mhabzda.todolist.domain.repository.TodoItemRepository
import javax.inject.Inject

class CreateTodoItemUseCase @Inject constructor(
    private val todoItemRepository: TodoItemRepository,
) {
    suspend fun invoke(title: String, description: String, iconUrl: String?): Result<Unit> =
        todoItemRepository.createItem(title = title, description = description, iconUrl = iconUrl)
}
