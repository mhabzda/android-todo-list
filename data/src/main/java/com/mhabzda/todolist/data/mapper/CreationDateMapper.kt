package com.mhabzda.todolist.data.mapper

import java.time.LocalDateTime

internal fun String?.toCreationDateTime(): LocalDateTime =
    LocalDateTime.parse(this?.substringBefore('+'))
