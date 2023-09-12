package com.mhabzda.todolist.data.mapper

import com.google.firebase.firestore.DocumentSnapshot
import com.mhabzda.todolist.data.mapper.TodoDocumentKeys.CREATION_DATE_KEY
import com.mhabzda.todolist.data.mapper.TodoDocumentKeys.TITLE_KEY
import javax.inject.Inject

internal class TodoDocumentFilter @Inject constructor() {
    fun filter(document: DocumentSnapshot): Boolean =
        !document.getString(TITLE_KEY).isNullOrEmpty() && document.getString(CREATION_DATE_KEY) != null
}
