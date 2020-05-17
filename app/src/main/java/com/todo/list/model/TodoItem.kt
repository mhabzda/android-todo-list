package com.todo.list.model

import org.joda.time.DateTime

data class TodoItem(
  val title: String,
  val description: String,
  val creationDate: DateTime,
  val iconUrl: String?
)