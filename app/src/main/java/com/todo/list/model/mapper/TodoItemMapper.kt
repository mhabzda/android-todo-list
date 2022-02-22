package com.todo.list.model.mapper

import com.todo.list.model.entities.TodoItem
import com.todo.list.model.mapper.TodoDocumentKeys.CREATION_DATE_KEY
import com.todo.list.model.mapper.TodoDocumentKeys.DESCRIPTION_KEY
import com.todo.list.model.mapper.TodoDocumentKeys.LOGO_URL_KEY
import com.todo.list.model.mapper.TodoDocumentKeys.TITLE_KEY
import javax.inject.Inject

class TodoItemMapper @Inject constructor() {
    fun map(todoItem: TodoItem): Map<String, String> {
        return with(todoItem) {
            val itemMap = mutableMapOf(
                TITLE_KEY to title,
                DESCRIPTION_KEY to description,
                CREATION_DATE_KEY to creationDate.toString()
            )
            if (!iconUrl.isNullOrEmpty()) itemMap[LOGO_URL_KEY] = iconUrl

            itemMap
        }
    }
}
