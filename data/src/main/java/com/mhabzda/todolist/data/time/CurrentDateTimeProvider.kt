package com.mhabzda.todolist.data.time

import java.time.LocalDateTime
import javax.inject.Inject

class CurrentDateTimeProvider @Inject constructor() {
    fun getCurrentDateTime(): LocalDateTime = LocalDateTime.now()
}