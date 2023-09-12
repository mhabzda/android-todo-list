package com.mhabzda.todolist.domain.model

import java.time.ZonedDateTime

data class TodoItem(
    val id: String,
    val title: String,
    val description: String,
    val creationDateTime: ZonedDateTime,
    val iconUrl: String?
)
