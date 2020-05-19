package com.todo.list.ui.item.edition

import com.todo.list.model.entities.TodoItem
import com.todo.list.model.repository.TodoRepository
import com.todo.list.ui.item.base.ItemBasePresenter
import com.todo.list.ui.schedulers.SchedulerProvider
import io.reactivex.Completable
import org.joda.time.DateTime
import javax.inject.Inject

class ItemEditionPresenter @Inject constructor(
  private val todoRepository: TodoRepository,
  view: ItemEditionContract.View,
  schedulerProvider: SchedulerProvider
) : ItemBasePresenter(view, schedulerProvider), ItemEditionContract.Presenter {
  override fun performItemOperation(title: String, description: String, iconUrl: String?): Completable {
    return todoRepository.deleteItem(TodoItem(title, description, DateTime(), iconUrl)) // pass previous dateTime
  }
}