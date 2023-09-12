package com.mhabzda.todolist.domain.model

import java.time.LocalDateTime

data class TodoItem(
    val id: String,
    val title: String,
    val description: String,
    // TODO use zoned date time
    val creationDate: LocalDateTime,
    val iconUrl: String?
)
