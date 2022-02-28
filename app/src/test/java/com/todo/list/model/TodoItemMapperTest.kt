package com.todo.list.model

import com.todo.list.model.entities.TodoItem
import com.todo.list.model.mapper.TodoItemMapper
import com.todo.list.testutilities.BaseInputProvider
import com.todo.list.testutilities.TimeZoneExtension
import com.todo.list.testutilities.createDocumentMap
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.extension.RegisterExtension
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ArgumentsSource

class TodoItemMapperTest {

    @ParameterizedTest
    @ArgumentsSource(InputProvider::class)
    fun `it should map item correctly `(input: Input) {
        val mapper = TodoItemMapper()
        val result = mapper.map(input.item)

        assertEquals(input.expectedMap, result)
    }

    class InputProvider : BaseInputProvider() {
        override val listInput: List<Any>
            get() = listOf(
                Input(
                    TodoItem("Buy", "desc", DateTime("2020-05-19T12:40:04.698Z"), "logo.com"),
                    createDocumentMap("Buy", "desc", "2020-05-19T12:40:04.698Z", "logo.com")
                ),
                Input(
                    TodoItem("Buy", "desc", DateTime("2020-05-19T12:40:04.698Z"), ""),
                    createDocumentMap("Buy", "desc", "2020-05-19T12:40:04.698Z", null)
                ),
                Input(
                    TodoItem("Buy", "desc", DateTime("2020-05-19T12:40:04.698Z"), ""),
                    createDocumentMap("Buy", "desc", "2020-05-19T12:40:04.698Z", null)
                )
            )
    }

    data class Input(val item: TodoItem, val expectedMap: Map<String, String>)

    companion object {
        @JvmField
        @RegisterExtension
        val timezoneExtension = TimeZoneExtension(DateTimeZone.UTC)
    }
}
