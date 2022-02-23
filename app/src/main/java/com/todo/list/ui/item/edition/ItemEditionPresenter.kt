package com.todo.list.ui.item.edition

import com.todo.list.model.entities.TodoItem
import com.todo.list.model.repository.TodoRepository
import com.todo.list.ui.item.base.ItemBasePresenter
import com.todo.list.ui.parcel.TodoItemParcelable
import org.joda.time.DateTime
import javax.inject.Inject

class ItemEditionPresenter @Inject constructor(
    private val todoRepository: TodoRepository,
    private val view: ItemEditionContract.View,
    private val todoItemParcelable: TodoItemParcelable
) : ItemBasePresenter(view), ItemEditionContract.Presenter {
    override fun initializeItemData() {
        with(todoItemParcelable) {
            view.fillItemData(title, description, iconUrl)
        }
    }

    override suspend fun performItemOperation(title: String, description: String, iconUrl: String?): Result<Unit> =
        todoRepository.editItem(TodoItem(title, description, DateTime(todoItemParcelable.creationDate), iconUrl))
}
