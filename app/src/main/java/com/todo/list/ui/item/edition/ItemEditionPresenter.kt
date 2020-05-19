package com.todo.list.ui.item.edition

import com.todo.list.model.entities.TodoItem
import com.todo.list.model.repository.TodoRepository
import com.todo.list.ui.item.base.ItemBasePresenter
import com.todo.list.ui.schedulers.SchedulerProvider
import io.reactivex.Completable
import javax.inject.Inject

class ItemEditionPresenter @Inject constructor(
  private val todoRepository: TodoRepository,
  view: ItemEditionContract.View,
  schedulerProvider: SchedulerProvider
) : ItemBasePresenter(view, schedulerProvider), ItemEditionContract.Presenter {
  override fun performItemOperation(todoItem: TodoItem): Completable {
    return Completable.complete()
    // edit item
  }
}