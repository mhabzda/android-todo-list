package com.todo.list.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import com.todo.list.model.repository.TodoRepository
import com.todo.list.ui.schedulers.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

class ListPresenter @Inject constructor(
  private val todoRepository: TodoRepository,
  private val schedulerProvider: SchedulerProvider,
  private val view: ListContract.View
) : ViewModel(), ListContract.Presenter {
  private val compositeDisposable = CompositeDisposable()

  override fun fetchItems() {
    compositeDisposable.add(todoRepository.getTodoItems()
      .subscribeOn(schedulerProvider.io())
      .observeOn(schedulerProvider.ui())
      .subscribeBy(
        onSuccess = {
          view.displayTodoList(it)
        },
        onError = {
          Log.e(TAG, "Error", it)
        }
      ))

    compositeDisposable.add(todoRepository.fetchTodoItems()
      .subscribeOn(schedulerProvider.io())
      .observeOn(schedulerProvider.ui())
      .subscribeBy(
        onNext = {
          Log.d(TAG, "Paged list loaded $it")
        },
        onError = {
          Log.e(TAG, "Error", it)
        }
      ))
  }

  override fun onCleared() {
    compositeDisposable.clear()
    super.onCleared()
  }

  companion object {
    private val TAG = ListPresenter::class.simpleName
  }
}