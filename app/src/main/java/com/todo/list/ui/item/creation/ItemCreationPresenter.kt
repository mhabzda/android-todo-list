package com.todo.list.ui.item.creation

import com.todo.list.model.entities.TodoItem
import com.todo.list.model.repository.TodoRepository
import com.todo.list.ui.item.base.ItemBasePresenter
import com.todo.list.ui.schedulers.SchedulerProvider
import io.reactivex.Completable
import javax.inject.Inject

class ItemCreationPresenter @Inject constructor(
    private val todoRepository: TodoRepository,
    view: ItemCreationContract.View,
    schedulerProvider: SchedulerProvider
) : ItemBasePresenter(view, schedulerProvider), ItemCreationContract.Presenter {
    override fun performItemOperation(title: String, description: String, iconUrl: String?): Completable {
        return todoRepository.saveItem(TodoItem.create(title, description, iconUrl))
    }
}
