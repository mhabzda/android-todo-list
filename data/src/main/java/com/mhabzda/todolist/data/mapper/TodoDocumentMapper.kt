package com.mhabzda.todolist.data.mapper

import com.google.firebase.firestore.DocumentSnapshot
import com.mhabzda.todolist.data.mapper.TodoDocumentKeys.CREATION_DATE_TIME_KEY
import com.mhabzda.todolist.data.mapper.TodoDocumentKeys.DESCRIPTION_KEY
import com.mhabzda.todolist.data.mapper.TodoDocumentKeys.LOGO_URL_KEY
import com.mhabzda.todolist.data.mapper.TodoDocumentKeys.TITLE_KEY
import com.mhabzda.todolist.data.time.CurrentTimeProvider
import com.mhabzda.todolist.domain.error.IncompleteDataException.ItemCreationTimeMissingException
import com.mhabzda.todolist.domain.error.IncompleteDataException.ItemTitleMissingException
import com.mhabzda.todolist.domain.model.TodoItem
import java.time.ZonedDateTime
import javax.inject.Inject

internal class TodoDocumentMapper @Inject constructor(
    private val currentTimeProvider: CurrentTimeProvider,
) {
    fun map(document: DocumentSnapshot) =
        TodoItem(
            id = document.id,
            title = document.getString(TITLE_KEY) ?: throw ItemTitleMissingException,
            description = document.getString(DESCRIPTION_KEY).orEmpty(),
            creationDateTime = document.getString(CREATION_DATE_TIME_KEY)?.toCreationDateTime() ?: throw ItemCreationTimeMissingException,
            iconUrl = document.getString(LOGO_URL_KEY),
        )

    private fun String.toCreationDateTime(): ZonedDateTime =
        ZonedDateTime
            .parse(this)
            .withZoneSameInstant(currentTimeProvider.getCurrentTimezone())
}
