package com.todo.list.model.repository

import androidx.paging.PagedList
import androidx.paging.RxPagedListBuilder
import com.google.firebase.firestore.CollectionReference
import com.todo.list.model.entities.TodoItem
import com.todo.list.model.mapper.TodoDocumentMapper
import com.todo.list.model.mapper.TodoDocumentMapper.Companion.TITLE_KEY
import com.todo.list.model.repository.model.NetworkState
import com.todo.list.model.repository.model.PagingObservable
import com.todo.list.model.repository.source.TodoItemsDataSourceFactory
import com.todo.list.utils.isNotNull
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class FirestoreTodoRepository @Inject constructor(
  private val todoCollection: CollectionReference,
  private val todoDocumentMapper: TodoDocumentMapper
) : TodoRepository {
  private val networkOperationSubject = PublishSubject.create<NetworkState>()
  private var dataSourceFactory: TodoItemsDataSourceFactory? = null

  override fun fetchTodoItems(pageSize: Int): PagingObservable {
    val dataSourceFactory = TodoItemsDataSourceFactory(todoCollection, todoDocumentMapper, networkOperationSubject)
    this.dataSourceFactory = dataSourceFactory

    val pagedListObservable = RxPagedListBuilder(
      dataSourceFactory,
      createPagingConfig(pageSize)
    ).buildObservable()

    return PagingObservable(pagedListObservable, networkOperationSubject.hide())
  }

  override fun refreshTodoItems() {
    dataSourceFactory?.dataSource?.invalidate()
  }

  override fun observeItemsChanges(): Observable<Any> {
    return Observable.create { emitter ->
      observeSnapshots(emitter)
    }
  }

  override fun deleteItem(item: TodoItem) {
    networkOperationSubject.onNext(NetworkState.Loading)
    todoCollection
      .whereEqualTo(TITLE_KEY, item.title)
      .get()
      .addOnSuccessListener {
        val documentRef = it.documents.first().reference
        documentRef.delete()
          .addOnSuccessListener { networkOperationSubject.onNext(NetworkState.Loaded) }
          .addOnFailureListener { error -> networkOperationSubject.onNext(NetworkState.Error(error)) }
      }
      .addOnFailureListener {
        networkOperationSubject.onNext(NetworkState.Error(it))
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

  private fun createPagingConfig(pageSize: Int): PagedList.Config {
    return PagedList.Config.Builder()
      .setEnablePlaceholders(false)
      .setPageSize(pageSize)
      .build()
  }
}