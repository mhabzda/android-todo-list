package com.mhabzda.todolist.domain.usecase

import com.mhabzda.todolist.domain.repository.TodoItemRepository
import javax.inject.Inject

class EditTodoItemUseCase @Inject constructor(
    private val todoItemRepository: TodoItemRepository,
) {
    suspend fun invoke(id: String, title: String, description: String, iconUrl: String?): Result<Unit> =
        todoItemRepository.editItem(id = id, title = title, description = description, iconUrl = iconUrl)
}