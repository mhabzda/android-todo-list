package com.todo.list.model.repository

import androidx.paging.PagedList
import androidx.paging.RxPagedListBuilder
import com.google.firebase.firestore.CollectionReference
import com.todo.list.model.entities.TodoItem
import com.todo.list.model.mapper.TodoDocumentMapper
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject

class FirestoreTodoRepository @Inject constructor(
  private val todoCollection: CollectionReference,
  private val todoDocumentMapper: TodoDocumentMapper,
  private val todoItemsDataSourceFactory: TodoItemsDataSourceFactory
) : TodoRepository {
  override fun getTodoItems(): Single<List<TodoItem>> {
    return Single.create { emitter ->
      todoCollection.get()
        .addOnSuccessListener { collection ->
          val items = collection.documents
            .map { document -> todoDocumentMapper.map(document) }
            .filter { item -> item.title.isNotEmpty() }

          emitter.onSuccess(items)
        }
        .addOnFailureListener {
          emitter.onError(it)
        }
    }
  }

  override fun fetchTodoItems(): Observable<PagedList<TodoItem>> {
    val pagingConfig = PagedList.Config.Builder()
      .setEnablePlaceholders(false)
      .setPageSize(PAGE_SIZE)
      .build()

    return RxPagedListBuilder(
      todoItemsDataSourceFactory,
      pagingConfig
    ).buildObservable()
  }

  companion object {
    private const val PAGE_SIZE = 5
  }
}