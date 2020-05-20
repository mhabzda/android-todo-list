package com.todo.list.ui

import com.todo.list.model.entities.TodoItem
import org.joda.time.DateTime
import org.joda.time.DateTimeZone

object TestData {
  val testTodoItem = TodoItem("title", "desc", DateTime("2020-05-19T12:40:04.698", DateTimeZone.UTC), "logo.com")
}