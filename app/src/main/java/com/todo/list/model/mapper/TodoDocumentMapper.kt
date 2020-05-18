package com.todo.list.model.mapper

import com.google.firebase.firestore.DocumentSnapshot
import com.todo.list.model.entities.TodoItem
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

  companion object {
    const val TITLE_KEY = "title"
    private const val DESCRIPTION_KEY = "description"
    private const val CREATION_DATE_KEY = "creationDate"
    private const val LOGO_URL_KEY = "logoUrl"
  }
}