package com.todo.list.testutilities

import com.google.firebase.firestore.DocumentSnapshot
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock

private const val titleKey = "title"
private const val descriptionKey = "description"
private const val creationDateKey = "creationDate"
private const val logoUrlKey = "logoUrl"

fun mockDocument(
  title: String? = null,
  description: String? = null,
  creationDate: String? = null,
  logoUrl: String? = null
): DocumentSnapshot {
  return mock {
    on { getString(titleKey) } doReturn title
    on { getString(descriptionKey) } doReturn description
    on { getString(creationDateKey) } doReturn creationDate
    on { getString(logoUrlKey) } doReturn logoUrl
  }
}

fun createDocumentMap(
  title: String,
  description: String,
  creationDate: String,
  logoUrl: String?
): Map<String, String> {
  val map = mutableMapOf(
    titleKey to title,
    descriptionKey to description,
    creationDateKey to creationDate
  )
  logoUrl?.let { map.put(logoUrlKey, it) }
  return map
}