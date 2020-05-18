package com.todo.list.model.repository.source

import androidx.paging.DataSource
import com.google.firebase.firestore.CollectionReference
import com.todo.list.model.entities.TodoItem
import com.todo.list.model.mapper.TodoDocumentMapper
import com.todo.list.model.repository.model.NetworkState
import io.reactivex.subjects.PublishSubject

class TodoItemsDataSourceFactory(
  private val collectionReference: CollectionReference,
  private val todoDocumentMapper: TodoDocumentMapper,
  private val networkOperationSubject: PublishSubject<NetworkState>
) : DataSource.Factory<Int, TodoItem>() {
  lateinit var dataSource: TodoItemsDataSource

  override fun create(): DataSource<Int, TodoItem> {
    val source = TodoItemsDataSource(collectionReference, todoDocumentMapper, networkOperationSubject)
    dataSource = source
    return source
  }
}