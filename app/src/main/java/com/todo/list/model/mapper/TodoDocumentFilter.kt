package com.todo.list.model.mapper

import com.google.firebase.firestore.DocumentSnapshot
import com.todo.list.model.mapper.TodoDocumentKeys.CREATION_DATE_KEY
import com.todo.list.model.mapper.TodoDocumentKeys.TITLE_KEY
import com.todo.list.utils.isNotNull
import javax.inject.Inject

class TodoDocumentFilter @Inject constructor() {
  fun filer(document: DocumentSnapshot): Boolean {
    return document.get(TITLE_KEY).isNotNull() && document.get(CREATION_DATE_KEY).isNotNull()
  }
}