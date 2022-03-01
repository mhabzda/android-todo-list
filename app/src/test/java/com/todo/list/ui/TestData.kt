package com.todo.list.ui

import com.todo.list.model.entities.TodoItem
import org.joda.time.DateTime
import org.joda.time.DateTimeZone

object TestData {
    val testItemId = DateTime("2020-05-19T12:40:04.698Z", DateTimeZone.UTC)
    val testTodoItem = TodoItem("title", "desc", testItemId, "logo.com")
}
