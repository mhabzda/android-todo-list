package com.todo.list.ui.list

import android.util.Log
import com.todo.list.model.entities.TodoItem
import com.todo.list.model.repository.TodoRepository
import com.todo.list.model.repository.model.NetworkState
import com.todo.list.ui.schedulers.SchedulerProvider
import com.todo.list.utils.EMPTY
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

class ListPresenter @Inject constructor(
  private val todoRepository: TodoRepository,
  private val schedulerProvider: SchedulerProvider,
  private val view: ListContract.View,
  private val router: ListContract.Router
) : ListContract.Presenter {
  private val compositeDisposable = CompositeDisposable()

  override fun observePagedData() {
    val pagingObservable = todoRepository.fetchTodoItems(PAGE_SIZE)
    compositeDisposable.add(pagingObservable.pagedList
      .observeOn(schedulerProvider.ui())
      .subscribeBy(
        onNext = {
          view.displayTodoList(it)
        },
        onError = {
          Log.e(TAG, "Error during paged list observation", it)
          view.displayError(it.message ?: EMPTY)
        }
      ))

    handleNetworkState(pagingObservable.networkState)
    observeItemsChanges()
  }

  override fun refreshItems() {
    todoRepository.refreshTodoItems()
  }

  override fun floatingButtonClicked() {
    router.openItemCreationView()
  }

  override fun itemLongClicked(item: TodoItem) {
    router.openDeleteItemConfirmationDialog {
      view.setRefreshingState(true)
      compositeDisposable.add(todoRepository.deleteItem(item)
        .observeOn(schedulerProvider.ui())
        .doOnTerminate { view.setRefreshingState(false) }
        .subscribeBy(
          onError = {
            view.displayError(it.message ?: EMPTY)
          }
        ))
    }
  }

  override fun clearResources() {
    compositeDisposable.clear()
  }

  private fun handleNetworkState(networkStateObservable: Observable<NetworkState>) {
    compositeDisposable.add(networkStateObservable
      .observeOn(schedulerProvider.ui())
      .subscribeBy {
        when (it) {
          NetworkState.Loading -> view.setRefreshingState(true)
          NetworkState.Loaded -> view.setRefreshingState(false)
          is NetworkState.Error -> {
            view.setRefreshingState(false)
            view.displayError(it.throwable.message ?: EMPTY)
          }
        }
      })
  }

  private fun observeItemsChanges() {
    compositeDisposable.add(todoRepository.observeItemsChanges()
      .subscribeBy {
        refreshItems()
      })
  }

  companion object {
    private val TAG = ListPresenter::class.simpleName
    private const val PAGE_SIZE = 5
  }
}