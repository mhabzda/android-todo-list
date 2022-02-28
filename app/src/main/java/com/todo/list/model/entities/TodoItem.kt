package com.todo.list.model.entities

import org.joda.time.DateTime

data class TodoItem(
    val title: String,
    val description: String,
    val creationDate: DateTime,
    val iconUrl: String
) {
    companion object {
        fun create(title: String, description: String, iconUrl: String): TodoItem {
            return TodoItem(title, description, DateTime.now(), iconUrl)
        }
    }
}
