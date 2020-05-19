package com.todo.list.model.repository

import androidx.paging.PagedList
import androidx.paging.RxPagedListBuilder
import com.google.firebase.firestore.CollectionReference
import com.todo.list.model.entities.TodoItem
import com.todo.list.model.mapper.TodoDocumentMapper.Companion.TITLE_KEY
import com.todo.list.model.mapper.TodoItemMapper
import com.todo.list.model.repository.model.PagingObservable
import com.todo.list.model.repository.source.TodoItemsDataSourceFactory
import com.todo.list.utils.isNotNull
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import javax.inject.Inject

class FirestoreTodoRepository @Inject constructor(
  private val todoItemsDataSourceFactory: TodoItemsDataSourceFactory,
  private val todoCollection: CollectionReference,
  private val todoItemMapper: TodoItemMapper
) : TodoRepository {
  override fun fetchTodoItems(pageSize: Int): PagingObservable {
    val pagedListObservable = RxPagedListBuilder(
      todoItemsDataSourceFactory,
      createPagingConfig(pageSize)
    ).buildObservable()
    val networkStateObservable = todoItemsDataSourceFactory.networkStateSubject.hide()

    return PagingObservable(pagedListObservable, networkStateObservable)
  }

  override fun refreshTodoItems() {
    todoItemsDataSourceFactory.dataSource?.invalidate()
  }

  override fun observeItemsChanges(): Observable<Any> {
    return Observable.create { emitter ->
      observeSnapshots(emitter)
    }
  }

  override fun deleteItem(item: TodoItem): Completable {
    return Completable.create { emitter ->
      todoCollection
        .whereEqualTo(TITLE_KEY, item.title)
        .get()
        .addOnSuccessListener {
          val documentRef = it.documents.first().reference
          documentRef.delete()
            .addOnSuccessListener { emitter.onComplete() }
            .addOnFailureListener { error -> emitter.onError(error) }
        }
        .addOnFailureListener {
          emitter.onError(it)
        }
    }
  }

  override fun saveItem(item: TodoItem): Completable {
    return Completable.create { emitter ->
      todoCollection
        .add(todoItemMapper.map(item))
        .addOnSuccessListener { emitter.onComplete() }
        .addOnFailureListener { emitter.onError(it) }
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