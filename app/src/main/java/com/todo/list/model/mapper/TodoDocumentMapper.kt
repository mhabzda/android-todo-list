package com.todo.list.model.mapper

import com.google.firebase.firestore.DocumentSnapshot
import com.todo.list.model.entities.TodoItem
import com.todo.list.model.mapper.TodoDocumentKeys.CREATION_DATE_KEY
import com.todo.list.model.mapper.TodoDocumentKeys.DESCRIPTION_KEY
import com.todo.list.model.mapper.TodoDocumentKeys.LOGO_URL_KEY
import com.todo.list.model.mapper.TodoDocumentKeys.TITLE_KEY
import javax.inject.Inject

class TodoDocumentMapper @Inject constructor() {
    fun map(document: DocumentSnapshot) =
        TodoItem(
            title = document.getString(TITLE_KEY).orEmpty(),
            description = document.getString(DESCRIPTION_KEY).orEmpty(),
            creationDate = document.getString(CREATION_DATE_KEY).toCreationDateTime(),
            iconUrl = document.getString(LOGO_URL_KEY).orEmpty()
        )
}
