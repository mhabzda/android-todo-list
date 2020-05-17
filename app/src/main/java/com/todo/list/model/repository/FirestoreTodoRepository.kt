package com.todo.list.model.repository

import com.google.firebase.firestore.CollectionReference
import com.todo.list.model.entities.TodoItem
import com.todo.list.model.mapper.TodoDocumentMapper
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class FirestoreTodoRepository @Inject constructor(
  private val todoCollection: CollectionReference,
  private val todoDocumentMapper: TodoDocumentMapper
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
}