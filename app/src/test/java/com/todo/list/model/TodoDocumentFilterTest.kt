package com.todo.list.model

import com.google.firebase.firestore.DocumentSnapshot
import com.todo.list.model.mapper.TodoDocumentFilter
import com.todo.list.testutilities.BaseInputProvider
import com.todo.list.testutilities.mockDocument
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ArgumentsSource

class TodoDocumentFilterTest {

    @ParameterizedTest
    @ArgumentsSource(InputProvider::class)
    fun `it should filter out invalid item`(input: Input) {
        val filter = TodoDocumentFilter()
        val result = filter.filter(input.document)

        assertEquals(input.expectedResult, result)
    }

    class InputProvider : BaseInputProvider() {
        override val listInput: List<Any>
            get() = listOf(
                Input(mockDocument(title = "Buy a book", creationDate = "2020-05-19T12:40:04.698"), true),
                Input(mockDocument(title = null, creationDate = "2020-05-19T12:40:04.698"), false),
                Input(mockDocument(title = "", creationDate = "2020-05-19T12:40:04.698"), false),
                Input(mockDocument(title = "Buy a book", creationDate = null), false),
                Input(mockDocument(title = null, creationDate = null), false)
            )
    }

    data class Input(val document: DocumentSnapshot, val expectedResult: Boolean)
}
