package com.mhabzda.todolist.util

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

fun ZonedDateTime.format(): String =
    format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))

fun <T> Result<T>.onTerminate(block: () -> Unit) {
    onSuccess { block.invoke() }
    onFailure { block.invoke() }
}
