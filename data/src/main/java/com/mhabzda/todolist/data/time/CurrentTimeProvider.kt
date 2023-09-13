package com.mhabzda.todolist.data.time

import java.time.ZoneId
import java.time.ZonedDateTime
import javax.inject.Inject

internal class CurrentTimeProvider @Inject constructor() {
    fun getCurrentDateTime(): ZonedDateTime = ZonedDateTime.now()
    fun getCurrentTimezone(): ZoneId = ZoneId.systemDefault()
}