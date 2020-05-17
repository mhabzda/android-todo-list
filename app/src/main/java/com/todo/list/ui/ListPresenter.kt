package com.todo.list.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import com.todo.list.model.repository.TodoRepository
import com.todo.list.ui.schedulers.SchedulerProvider
import io.reactivex.rxjava3.kotlin.subscribeBy
import javax.inject.Inject

class ListPresenter @Inject constructor(
  private val todoRepository: TodoRepository,
  private val schedulerProvider: SchedulerProvider
) : ViewModel(), ListContract.Presenter {
  lateinit var view: ListContract.View

  override fun attachView(view: ListContract.View) {
    this.view = view
  }

  override fun fetchItems() {
    todoRepository.getTodoItems()
      .subscribeOn(schedulerProvider.io())
      .observeOn(schedulerProvider.ui())
      .subscribeBy(
        onSuccess = {
          view.displayTodoList(it)
        },
        onError = {
          Log.e(ListActivity::class.simpleName, "Error", it)
        }
      )
  }
}