package com.todo.list.ui

import com.todo.list.model.entities.TodoItem
import com.todo.list.ui.parcel.TodoItemParcelable
import org.joda.time.DateTime
import org.joda.time.DateTimeZone

object TestData {
  val testTodoItem = TodoItem("title", "desc", DateTime("2020-05-19T12:40:04.698Z", DateTimeZone.UTC), "logo.com")
  val testTodoItemParcelable = TodoItemParcelable("title", "desc", "2020-05-19T12:40:04.698Z", "logo.com")
}