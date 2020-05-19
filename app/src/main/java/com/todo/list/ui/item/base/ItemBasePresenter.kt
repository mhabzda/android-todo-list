package com.todo.list.ui.item.base

import com.todo.list.model.entities.TodoItem
import com.todo.list.ui.schedulers.SchedulerProvider
import com.todo.list.utils.EMPTY
import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy

abstract class ItemBasePresenter(
  private val view: ItemBaseContract.View,
  private val schedulerProvider: SchedulerProvider
) : ItemBaseContract.Presenter {
  private val compositeDisposable = CompositeDisposable()

  override fun itemButtonClicked(title: String, description: String, iconUrl: String?) {
    if (title.isEmpty()) {
      view.displayEmptyTitleError()
      return
    }

    view.toggleLoading(true)
    compositeDisposable.add(performItemOperation(TodoItem.create(title, description, iconUrl))
      .observeOn(schedulerProvider.ui())
      .doOnTerminate { view.toggleLoading(false) }
      .subscribeBy(
        onComplete = {
          view.displayConfirmationMessage()
          view.close()
        },
        onError = {
          view.displayError(it.message ?: EMPTY)
        }
      ))
  }

  override fun releaseResources() {
    compositeDisposable.clear()
  }

  abstract fun performItemOperation(todoItem: TodoItem): Completable
}