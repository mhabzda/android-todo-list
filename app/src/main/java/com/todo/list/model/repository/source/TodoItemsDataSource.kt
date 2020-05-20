package com.todo.list.model.repository.source

import androidx.paging.PositionalDataSource
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.todo.list.model.entities.TodoItem
import com.todo.list.model.mapper.TodoDocumentFilter
import com.todo.list.model.mapper.TodoDocumentMapper
import com.todo.list.model.repository.model.NetworkState
import io.reactivex.subjects.PublishSubject

class TodoItemsDataSource(
  private val todoCollection: CollectionReference,
  private val todoDocumentMapper: TodoDocumentMapper,
  private val todoDocumentFilter: TodoDocumentFilter,
  private val networkOperationSubject: PublishSubject<NetworkState>
) : PositionalDataSource<TodoItem>() {
  private lateinit var lastLoadedItem: DocumentSnapshot

  override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<TodoItem>) {
    loadItems(
      query = {
        todoCollection
          .limit(params.pageSize.toLong())
          .get()
      },
      callbackAction = {
        callback.onResult(it, params.requestedStartPosition)
      })
  }

  override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<TodoItem>) {
    loadItems(
      query = {
        todoCollection
          .startAfter(lastLoadedItem)
          .limit(params.loadSize.toLong())
          .get()
      },
      callbackAction = {
        callback.onResult(it)
      }
    )
  }

  private fun loadItems(query: () -> Task<QuerySnapshot>, callbackAction: (list: List<TodoItem>) -> Unit) {
    networkOperationSubject.onNext(NetworkState.Loading)
    try {
      val collection = Tasks.await(query())
      cacheLastLoadedElement(collection)
      callbackAction(formatItems(collection))

      networkOperationSubject.onNext(NetworkState.Loaded)
    } catch (exception: Throwable) {
      networkOperationSubject.onNext(NetworkState.Error(exception))
    }
  }

  private fun cacheLastLoadedElement(collection: QuerySnapshot) {
    val size = collection.size()
    if (size > 0) {
      lastLoadedItem = collection.documents[size - 1]
    }
  }

  private fun formatItems(collection: QuerySnapshot): List<TodoItem> {
    return collection.documents
      .filter { document -> todoDocumentFilter.filter(document) }
      .map { document -> todoDocumentMapper.map(document) }
  }
}