package com.mhabzda.todolist.data.integration

import com.mhabzda.todolist.data.mapper.TodoDocumentMapper
import com.mhabzda.todolist.data.mapper.TodoItemMapper
import com.mhabzda.todolist.data.mockDocumentFromMap
import com.mhabzda.todolist.data.time.CurrentTimeProvider
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.given
import org.mockito.kotlin.mock
import java.time.ZoneId
import java.time.ZonedDateTime

class ItemTimezoneIntegrationTest {

    private val mockCurrentTimeProvider: CurrentTimeProvider = mock()

    private val todoItemMapper = TodoItemMapper()
    private val documentMapper = TodoDocumentMapper(mockCurrentTimeProvider)

    @Test
    fun `GIVEN item saved in one timezone WHEN read in different user timezone THEN format in local user timezone`() {
        val creationTime = ZonedDateTime.parse("2023-09-13T17:23:34.000000234+02:00[Europe/Paris]")
        given(mockCurrentTimeProvider.getCurrentTimezone()).willReturn(ZoneId.of("Europe/Paris"))
        val documentMap = todoItemMapper.map(
            title = "title",
            description = "desc",
            iconUrl = null,
            creationTime = creationTime,
        )

        given(mockCurrentTimeProvider.getCurrentTimezone()).willReturn(ZoneId.of("America/Chicago"))
        val item = documentMapper.map(mockDocumentFromMap(documentMap))

        assertEquals(ZonedDateTime.parse("2023-09-13T10:23:34.000000234-05:00[America/Chicago]"), item.creationDateTime)
    }

    @Test
    fun `GIVEN item saved in one timezone previous day WHEN read in different timezone next day THEN format next day`() {
        val creationTime = ZonedDateTime.parse("2023-09-14T23:23:34.000000234-05:00[America/Chicago]")
        given(mockCurrentTimeProvider.getCurrentTimezone()).willReturn(ZoneId.of("America/Chicago"))
        val documentMap = todoItemMapper.map(
            title = "title",
            description = "desc",
            iconUrl = null,
            creationTime = creationTime,
        )

        given(mockCurrentTimeProvider.getCurrentTimezone()).willReturn(ZoneId.of("Europe/Paris"))
        val item = documentMapper.map(mockDocumentFromMap(documentMap))

        assertEquals(ZonedDateTime.parse("2023-09-15T06:23:34.000000234+02:00[Europe/Paris]"), item.creationDateTime)
    }

    @Test
    fun `GIVEN item saved in one timezone next day WHEN read in different timezone previous day THEN format previous day`() {
        val creationTime = ZonedDateTime.parse("2023-09-15T06:23:34.000000234+02:00[Europe/Paris]")
        given(mockCurrentTimeProvider.getCurrentTimezone()).willReturn(ZoneId.of("Europe/Paris"))
        val documentMap = todoItemMapper.map(
            title = "title",
            description = "desc",
            iconUrl = null,
            creationTime = creationTime,
        )

        given(mockCurrentTimeProvider.getCurrentTimezone()).willReturn(ZoneId.of("America/Chicago"))
        val item = documentMapper.map(mockDocumentFromMap(documentMap))

        assertEquals(ZonedDateTime.parse("2023-09-14T23:23:34.000000234-05:00[America/Chicago]"), item.creationDateTime)
    }
}
