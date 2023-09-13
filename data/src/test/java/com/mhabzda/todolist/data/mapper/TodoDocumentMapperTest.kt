package com.mhabzda.todolist.data.mapper

import com.mhabzda.todolist.data.time.CurrentTimeProvider
import com.mhabzda.todolist.data.utils.mockDocument
import com.mhabzda.todolist.domain.error.IncompleteDataException.ItemCreationTimeMissingException
import com.mhabzda.todolist.domain.error.IncompleteDataException.ItemTitleMissingException
import com.mhabzda.todolist.domain.model.TodoItem
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrowsExactly
import org.junit.jupiter.api.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import java.time.ZoneId
import java.time.ZonedDateTime

class TodoDocumentMapperTest {

    private val mockCurrentTimeProvider: CurrentTimeProvider = mock {
        on { getCurrentTimezone() } doReturn ZoneId.of("Europe/Paris")
    }

    private val mapper = TodoDocumentMapper(mockCurrentTimeProvider)

    private val timeString = "2023-09-13T17:23:34.000000234+02:00[Europe/Paris]"
    private val time = ZonedDateTime.parse(timeString)

    @Test
    fun `GIVEN all data present WHEN map THEN return todo item`() {
        val givenDocument = mockDocument(
            id = "1",
            title = "Buy",
            description = "desc",
            creationTime = timeString,
            iconUrl = "icon.com",
        )

        val result = mapper.map(givenDocument)

        assertEquals(
            TodoItem(
                id = "1",
                title = "Buy",
                description = "desc",
                creationDateTime = time,
                iconUrl = "icon.com",
            ),
            result,
        )
    }

    @Test
    fun `GIVEN empty description and icon url WHEN map THEN return todo item`() {
        val givenDocument = mockDocument(
            id = "1",
            title = "Buy",
            description = "",
            creationTime = timeString,
            iconUrl = null,
        )

        val result = mapper.map(givenDocument)

        assertEquals(
            TodoItem(
                id = "1",
                title = "Buy",
                description = "",
                creationDateTime = time,
                iconUrl = null,
            ),
            result,
        )
    }

    @Test
    fun `GIVEN null title WHEN map THEN throw exception`() {
        val givenDocument = mockDocument(
            id = "1",
            title = null,
            description = "",
            creationTime = timeString,
            iconUrl = null,
        )

        assertThrowsExactly(ItemTitleMissingException::class.java) { mapper.map(givenDocument) }
    }

    @Test
    fun `GIVEN null creation time WHEN map THEN throw exception`() {
        val givenDocument = mockDocument(
            id = "1",
            title = "Buy",
            description = "",
            creationTime = null,
            iconUrl = null,
        )

        assertThrowsExactly(ItemCreationTimeMissingException::class.java) { mapper.map(givenDocument) }
    }
}
