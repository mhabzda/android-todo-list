package com.todo.list.model.mapper

import org.joda.time.DateTime

fun String?.toCreationDateTime() =
    DateTime(this)

fun DateTime.toCreationDateString() =
    toString()
