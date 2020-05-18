package com.todo.list.model.repository.source

import androidx.paging.DataSource
import com.google.firebase.firestore.CollectionReference
import com.todo.list.model.entities.TodoItem
import com.todo.list.model.mapper.TodoDocumentMapper
import com.todo.list.model.repository.model.NetworkState
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class TodoItemsDataSourceFactory @Inject constructor(
  private val collectionReference: CollectionReference,
  private val todoDocumentMapper: TodoDocumentMapper
) : DataSource.Factory<Int, TodoItem>() {
  val networkOperationSubject = PublishSubject.create<NetworkState>()
  lateinit var dataSource: TodoItemsDataSource

  override fun create(): DataSource<Int, TodoItem> {
    val source = TodoItemsDataSource(collectionReference, todoDocumentMapper, networkOperationSubject)
    dataSource = source
    return source
  }
}