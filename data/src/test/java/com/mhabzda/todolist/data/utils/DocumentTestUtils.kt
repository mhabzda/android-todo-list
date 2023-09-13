package com.mhabzda.todolist.data.utils

import com.google.firebase.firestore.DocumentSnapshot
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

private const val TITLE_KEY = "title"
private const val DESCRIPTION_KEY = "description"
private const val CREATION_TIME_KEY = "creationDate"
private const val ICON_URL_KEY = "logoUrl"

fun mockDocument(
    id: String,
    title: String? = null,
    description: String? = null,
    creationTime: String? = null,
    iconUrl: String? = null
): DocumentSnapshot =
    mock {
        on { this.id } doReturn id
        on { getString(TITLE_KEY) } doReturn title
        on { getString(DESCRIPTION_KEY) } doReturn description
        on { getString(CREATION_TIME_KEY) } doReturn creationTime
        on { getString(ICON_URL_KEY) } doReturn iconUrl
    }

fun createDocumentMap(
    title: String,
    description: String,
    creationTime: String? = null,
    iconUrl: String? = null
): Map<String, String> {
    val map = mutableMapOf(
        TITLE_KEY to title,
        DESCRIPTION_KEY to description,
    )
    iconUrl?.let { map[ICON_URL_KEY] = it }
    creationTime?.let { map[CREATION_TIME_KEY] = it }
    return map
}

fun mockDocumentFromMap(documentMap: Map<String, String>): DocumentSnapshot =
    mock {
        on { id } doReturn "xhayjsuitshyq"
        on { getString(TITLE_KEY) } doReturn documentMap[TITLE_KEY]
        on { getString(DESCRIPTION_KEY) } doReturn documentMap[DESCRIPTION_KEY]
        on { getString(CREATION_TIME_KEY) } doReturn documentMap[CREATION_TIME_KEY]
        on { getString(ICON_URL_KEY) } doReturn documentMap[ICON_URL_KEY]
    }
