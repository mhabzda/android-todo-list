package com.todo.list.model

import com.google.firebase.firestore.DocumentSnapshot
import com.todo.list.model.entities.TodoItem
import com.todo.list.model.mapper.TodoDocumentMapper
import com.todo.list.testutilities.BaseInputProvider
import com.todo.list.testutilities.FixedTimeExtension
import com.todo.list.testutilities.mockDocument
import org.joda.time.DateTime
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.extension.RegisterExtension
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ArgumentsSource

class TodoDocumentMapperTest {

  companion object {
    private const val FIXED_DATE_TIME = "2020-05-19T12:40:04.698"

    @JvmField
    @RegisterExtension
    val fixedTimeExtension = FixedTimeExtension(FIXED_DATE_TIME)
  }

  @ParameterizedTest
  @ArgumentsSource(InputProvider::class)
  fun `it should map document correctly`(input: Input) {
    val mapper = TodoDocumentMapper()
    val result = mapper.map(input.document)

    assertEquals(input.expectedItem, result)
  }

  class InputProvider : BaseInputProvider() {
    override val listInput: List<Any>
      get() = listOf(
        Input(
          mockDocument("Buy", "desc", dateTime, "logo.com"),
          TodoItem("Buy", "desc", DateTime(dateTime), "logo.com")
        ),
        Input(
          mockDocument("", "", dateTime, "logo.com"),
          TodoItem("", "", DateTime(dateTime), "logo.com")
        ),
        Input(
          mockDocument(null, null, dateTime, "logo.com"),
          TodoItem("", "", DateTime(dateTime), "logo.com")
        ),
        Input(
          mockDocument("Buy", "desc", null, "logo.com"),
          TodoItem("Buy", "desc", DateTime(FIXED_DATE_TIME), "logo.com")
        ),
        Input(
          mockDocument("Buy", "desc", dateTime, null),
          TodoItem("Buy", "desc", DateTime(dateTime), null)
        )
      )

    private val dateTime = "2020-05-19T10:40:04.698"
  }

  data class Input(val document: DocumentSnapshot, val expectedItem: TodoItem)
}