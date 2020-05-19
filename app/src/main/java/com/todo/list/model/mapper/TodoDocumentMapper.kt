package com.todo.list.model.mapper

import com.google.firebase.firestore.DocumentSnapshot
import com.todo.list.model.entities.TodoItem
import com.todo.list.utils.EMPTY
import com.todo.list.utils.isNotNull
import org.joda.time.DateTime
import javax.inject.Inject

class TodoDocumentMapper @Inject constructor() {
  fun map(document: DocumentSnapshot): TodoItem {
    val creationDateString = document.getString(CREATION_DATE_KEY)
    val creationDate = if (creationDateString.isNotNull()) DateTime(creationDateString) else null

    return TodoItem(
      title = document.getString(TITLE_KEY) ?: EMPTY,
      description = document.getString(DESCRIPTION_KEY) ?: EMPTY,
      creationDate = creationDate,
      iconUrl = document.getString(LOGO_URL_KEY)
    )
  }

  companion object {
    const val TITLE_KEY = "title"
    const val DESCRIPTION_KEY = "description"
    const val CREATION_DATE_KEY = "creationDate"
    const val LOGO_URL_KEY = "logoUrl"
  }
}