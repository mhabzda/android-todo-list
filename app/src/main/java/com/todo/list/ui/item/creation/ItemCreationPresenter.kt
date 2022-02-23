package com.todo.list.ui.item.creation

import com.todo.list.model.entities.TodoItem
import com.todo.list.model.repository.TodoRepository
import com.todo.list.ui.item.base.ItemBasePresenter
import javax.inject.Inject

class ItemCreationPresenter @Inject constructor(
    private val todoRepository: TodoRepository,
    view: ItemCreationContract.View
) : ItemBasePresenter(view), ItemCreationContract.Presenter {
    override suspend fun performItemOperation(title: String, description: String, iconUrl: String?): Result<Unit> =
        todoRepository.saveItem(TodoItem.create(title, description, iconUrl))
}
