package com.mhabzda.todolist.data.mapper

import com.mhabzda.todolist.data.mapper.TodoDocumentKeys.CREATION_DATE_TIME_KEY
import com.mhabzda.todolist.data.mapper.TodoDocumentKeys.DESCRIPTION_KEY
import com.mhabzda.todolist.data.mapper.TodoDocumentKeys.LOGO_URL_KEY
import com.mhabzda.todolist.data.mapper.TodoDocumentKeys.TITLE_KEY
import java.time.ZonedDateTime
import javax.inject.Inject

internal class TodoItemMapper @Inject constructor() {
    fun map(
        title: String,
        description: String,
        iconUrl: String?,
        creationTime: ZonedDateTime? = null
    ): Map<String, String> {
        val itemMap = mutableMapOf(
            TITLE_KEY to title,
            DESCRIPTION_KEY to description,
        )
        iconUrl?.let { itemMap[LOGO_URL_KEY] = it }
        creationTime?.let { itemMap[CREATION_DATE_TIME_KEY] = it.toString() }

        return itemMap
    }
}
