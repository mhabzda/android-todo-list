package com.todo.list.model.mapper

import com.google.firebase.firestore.DocumentSnapshot
import com.todo.list.model.TodoItem
import com.todo.list.utils.EMPTY
import org.joda.time.DateTime
import javax.inject.Inject

class TodoDocumentMapper @Inject constructor() {
  fun map(document: DocumentSnapshot): TodoItem {
    return TodoItem(
      document.getString("title") ?: EMPTY,
      document.getString("description") ?: EMPTY,
      DateTime.now(),
      document.getString("logoUrl")
    )
  }
}