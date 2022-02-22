package com.todo.list.integration

import com.todo.list.model.entities.TodoItem
import com.todo.list.model.mapper.TodoDocumentMapper
import com.todo.list.model.mapper.TodoItemMapper
import com.todo.list.testutilities.FixedTimeExtension
import com.todo.list.testutilities.TimeZoneExtension
import com.todo.list.testutilities.mockDocumentFromMap
import com.todo.list.utils.formatDateHour
import org.joda.time.DateTimeZone
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class ItemTimezoneIntegrationTest {

    companion object {
        private const val FIXED_DATE_TIME = "2020-05-19T12:40:04.698Z"

        @JvmField
        @RegisterExtension
        val timezoneExtension = TimeZoneExtension(DateTimeZone.UTC)

        @JvmField
        @RegisterExtension
        val fixedTimeExtension = FixedTimeExtension(FIXED_DATE_TIME)
    }

    @Test
    fun `given item saved in one timezone when read in different user timezone then format in local user timezone`() {
        val itemMapper = TodoItemMapper()
        val documentMapper = TodoDocumentMapper()

        DateTimeZone.setDefault(DateTimeZone.forOffsetHours(7))
        val documentMap = itemMapper.map(TodoItem.create("title", "desc", null))

        DateTimeZone.setDefault(DateTimeZone.forOffsetHours(-3))
        val item = documentMapper.map(mockDocumentFromMap(documentMap))

        assertEquals("19.05.2020 09:40", item.creationDate.formatDateHour())
    }

    @Test
    fun `given item saved in one timezone previous day when read in different timezone next day then format next day`() {
        val itemMapper = TodoItemMapper()
        val documentMapper = TodoDocumentMapper()

        DateTimeZone.setDefault(DateTimeZone.forOffsetHours(8))
        val documentMap = itemMapper.map(TodoItem.create("title", "desc", null))

        DateTimeZone.setDefault(DateTimeZone.forOffsetHours(19))
        val item = documentMapper.map(mockDocumentFromMap(documentMap))

        assertEquals("20.05.2020 07:40", item.creationDate.formatDateHour())
    }

    @Test
    fun `given item saved in one timezone next day when read in different timezone previous day then format previous day`() {
        val itemMapper = TodoItemMapper()
        val documentMapper = TodoDocumentMapper()

        DateTimeZone.setDefault(DateTimeZone.forOffsetHours(-4))
        val documentMap = itemMapper.map(TodoItem.create("title", "desc", null))

        DateTimeZone.setDefault(DateTimeZone.forOffsetHours(-15))
        val item = documentMapper.map(mockDocumentFromMap(documentMap))

        assertEquals("18.05.2020 21:40", item.creationDate.formatDateHour())
    }
}
