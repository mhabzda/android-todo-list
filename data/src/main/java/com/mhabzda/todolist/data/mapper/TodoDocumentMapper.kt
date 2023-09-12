package com.mhabzda.todolist.data.mapper

import com.google.firebase.firestore.DocumentSnapshot
import com.mhabzda.todolist.data.mapper.TodoDocumentKeys.CREATION_DATE_KEY
import com.mhabzda.todolist.data.mapper.TodoDocumentKeys.DESCRIPTION_KEY
import com.mhabzda.todolist.data.mapper.TodoDocumentKeys.LOGO_URL_KEY
import com.mhabzda.todolist.data.mapper.TodoDocumentKeys.TITLE_KEY
import com.mhabzda.todolist.domain.model.TodoItem
import javax.inject.Inject

internal class TodoDocumentMapper @Inject constructor() {
    fun map(document: DocumentSnapshot) =
        TodoItem(
            id = document.id,
            title = document.getString(TITLE_KEY).orEmpty(),
            description = document.getString(DESCRIPTION_KEY).orEmpty(),
            creationDate = document.getString(CREATION_DATE_KEY).toCreationDateTime(),
            iconUrl = document.getString(LOGO_URL_KEY)
        )
}
