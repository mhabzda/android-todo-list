package com.mhabzda.todolist.data.mapper

import com.mhabzda.todolist.data.createDocumentMap
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime

class TodoItemMapperTest {

    private val mapper = TodoItemMapper()

    @Test
    fun `GIVEN all data present WHEN map THEN return full map`() {
        val result = mapper.map(
            title = "Buy",
            description = "desc",
            iconUrl = "icon.com",
            creationTime = ZonedDateTime.parse("2023-09-13T17:23:34.000000234+02:00[Europe/Paris]"),
        )

        assertEquals(
            createDocumentMap(
                title = "Buy",
                description = "desc",
                creationDateTime = "2023-09-13T17:23:34.000000234+02:00[Europe/Paris]",
                iconUrl = "icon.com",
            ),
            result,
        )
    }

    @Test
    fun `GIVEN icon url and creation time absent WHEN map THEN return partial map`() {
        val result = mapper.map(
            title = "Buy",
            description = "desc",
            iconUrl = null,
            creationTime = null,
        )

        assertEquals(
            createDocumentMap(
                title = "Buy",
                description = "desc",
            ),
            result,
        )
    }
}
