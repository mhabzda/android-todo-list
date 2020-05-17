package com.todo.list.model.repository

import androidx.paging.PositionalDataSource
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.todo.list.model.entities.TodoItem
import com.todo.list.model.mapper.TodoDocumentMapper
import javax.inject.Inject

class TodoItemsDataSource @Inject constructor(
  private val todoCollection: CollectionReference,
  private val todoDocumentMapper: TodoDocumentMapper
) : PositionalDataSource<TodoItem>() {
  lateinit var lastLoadedItem: DocumentSnapshot

  override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<TodoItem>) {
    val collection = Tasks.await(
      todoCollection
        .startAfter(lastLoadedItem)
        .limit(params.loadSize.toLong())
        .get()
    )

    lastLoadedItem = collection.documents[collection.size() - 1]
    val items = formatItems(collection)
    callback.onResult(items)
  }

  override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<TodoItem>) {
    val collection = Tasks.await(
      todoCollection
        .limit(params.pageSize.toLong())
        .get()
    )

    lastLoadedItem = collection.documents[collection.size() - 1]
    val items = formatItems(collection)
    callback.onResult(items, params.requestedStartPosition)
  }

  private fun formatItems(collection: QuerySnapshot): List<TodoItem> {
    return collection.documents
      .map { document -> todoDocumentMapper.map(document) }
      .filter { item -> item.title.isNotEmpty() }
  }
}