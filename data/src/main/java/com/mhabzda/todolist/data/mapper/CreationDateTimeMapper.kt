package com.mhabzda.todolist.data.mapper

import java.time.ZonedDateTime

internal fun String?.toCreationDateTime(): ZonedDateTime =
    ZonedDateTime.parse(this)
