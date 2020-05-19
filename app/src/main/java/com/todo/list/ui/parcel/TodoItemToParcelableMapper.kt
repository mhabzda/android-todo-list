package com.todo.list.ui.parcel

import com.todo.list.model.entities.TodoItem
import javax.inject.Inject

class TodoItemToParcelableMapper @Inject constructor() {
  fun map(todoItem: TodoItem): TodoItemParcelable {
    return TodoItemParcelable(
      title = todoItem.title,
      description = todoItem.description,
      creationDate = todoItem.creationDate.toString(),
      iconUrl = todoItem.iconUrl
    )
  }
}