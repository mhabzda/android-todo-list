package com.todo.list.model.repository

import androidx.paging.PagedList
import androidx.paging.RxPagedListBuilder
import com.google.firebase.firestore.CollectionReference
import com.todo.list.model.repository.model.PagingObservable
import com.todo.list.model.repository.source.TodoItemsDataSourceFactory
import com.todo.list.utils.isNotNull
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import javax.inject.Inject

class FirestoreTodoRepository @Inject constructor(
  private val todoItemsDataSourceFactory: TodoItemsDataSourceFactory,
  private val todoCollection: CollectionReference
) : TodoRepository {
  override fun fetchTodoItems(): PagingObservable {
    val pagedListObservable = RxPagedListBuilder(
      todoItemsDataSourceFactory,
      createPagingConfig()
    ).buildObservable()

    val networkStateObservable = todoItemsDataSourceFactory.networkOperationSubject.hide()

    return PagingObservable(pagedListObservable, networkStateObservable)
  }

  override fun refreshTodoItems() {
    todoItemsDataSourceFactory.dataSource.invalidate()
  }

  override fun observeItemsChanges(): Observable<Any> {
    return Observable.create { emitter ->
      observeSnapshots(emitter)
    }
  }

  private fun observeSnapshots(emitter: ObservableEmitter<Any>) {
    val registration = todoCollection.addSnapshotListener { querySnapshot, exception ->
      if (exception.isNotNull()) {
        return@addSnapshotListener
      }

      if (querySnapshot.isNotNull()) {
        emitter.onNext(Any())
      }
    }

    emitter.setCancellable {
      registration.remove()
    }
  }

  private fun createPagingConfig(): PagedList.Config {
    return PagedList.Config.Builder()
      .setEnablePlaceholders(false)
      .setPageSize(PAGE_SIZE)
      .build()
  }

  companion object {
    private const val PAGE_SIZE = 5
  }
}