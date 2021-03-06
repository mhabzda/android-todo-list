package com.todo.list.model.mapper

import com.google.firebase.firestore.DocumentSnapshot
import com.todo.list.model.entities.TodoItem
import com.todo.list.model.mapper.TodoDocumentKeys.CREATION_DATE_KEY
import com.todo.list.model.mapper.TodoDocumentKeys.DESCRIPTION_KEY
import com.todo.list.model.mapper.TodoDocumentKeys.LOGO_URL_KEY
import com.todo.list.model.mapper.TodoDocumentKeys.TITLE_KEY
import com.todo.list.utils.EMPTY
import org.joda.time.DateTime
import javax.inject.Inject

class TodoDocumentMapper @Inject constructor() {
  fun map(document: DocumentSnapshot): TodoItem {
    return TodoItem(
      title = document.getString(TITLE_KEY) ?: EMPTY,
      description = document.getString(DESCRIPTION_KEY) ?: EMPTY,
      creationDate = DateTime(document.getString(CREATION_DATE_KEY)),
      iconUrl = document.getString(LOGO_URL_KEY)
    )
  }
}