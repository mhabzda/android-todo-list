package com.mhabzda.todolist

import com.mhabzda.todolist.domain.model.TodoItem
import java.time.ZonedDateTime

object TestData {
    val testTodoItem = TodoItem(
        id = "11",
        title = "title",
        description = "desc",
        creationDateTime = ZonedDateTime.parse("2023-09-13T17:23:34.000000234+02:00[Europe/Paris]"),
        iconUrl = "logo.com",
    )
}
