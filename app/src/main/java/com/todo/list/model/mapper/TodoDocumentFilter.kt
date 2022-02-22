package com.todo.list.model.mapper

import com.google.firebase.firestore.DocumentSnapshot
import com.todo.list.model.mapper.TodoDocumentKeys.CREATION_DATE_KEY
import com.todo.list.model.mapper.TodoDocumentKeys.TITLE_KEY
import com.todo.list.utils.isNotNull
import javax.inject.Inject

class TodoDocumentFilter @Inject constructor() {
    fun filter(document: DocumentSnapshot): Boolean {
        return !document.getString(TITLE_KEY).isNullOrEmpty() && document.getString(CREATION_DATE_KEY).isNotNull()
    }
}
