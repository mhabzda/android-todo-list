package com.todo.list.model.mapper

import com.todo.list.model.entities.TodoItem
import com.todo.list.model.mapper.TodoDocumentMapper.Companion.CREATION_DATE_KEY
import com.todo.list.model.mapper.TodoDocumentMapper.Companion.DESCRIPTION_KEY
import com.todo.list.model.mapper.TodoDocumentMapper.Companion.LOGO_URL_KEY
import com.todo.list.model.mapper.TodoDocumentMapper.Companion.TITLE_KEY
import com.todo.list.utils.isNotNull
import javax.inject.Inject

class TodoItemMapper @Inject constructor() {
  fun map(todoItem: TodoItem): Map<String, String> {
    return with(todoItem) {
      val itemMap = mutableMapOf(
        TITLE_KEY to title,
        DESCRIPTION_KEY to description
      )
      if (creationDate.isNotNull()) itemMap[CREATION_DATE_KEY] = creationDate.toString()
      if (!iconUrl.isNullOrEmpty()) itemMap[LOGO_URL_KEY] = iconUrl

      itemMap
    }
  }
}