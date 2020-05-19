package com.todo.list.ui.item.creation

import com.todo.list.model.entities.TodoItem
import com.todo.list.model.repository.TodoRepository
import com.todo.list.ui.schedulers.SchedulerProvider
import com.todo.list.utils.EMPTY
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

class ItemCreationPresenter @Inject constructor(
  private val view: ItemCreationContract.View,
  private val schedulerProvider: SchedulerProvider,
  private val todoRepository: TodoRepository
) : ItemCreationContract.Presenter {
  private val compositeDisposable = CompositeDisposable()

  override fun saveItemButtonClicked(title: String, description: String, iconUrl: String?) {
    if (title.isEmpty()) {
      view.displayEmptyTitleError()
      return
    }

    view.toggleLoading(true)
    compositeDisposable.add(todoRepository.saveItem(TodoItem.create(title, description, iconUrl))
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
}