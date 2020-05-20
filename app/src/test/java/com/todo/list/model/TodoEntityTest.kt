package com.todo.list.model

import com.todo.list.model.entities.TodoItem
import com.todo.list.testutilities.FixedTimeExtension
import org.joda.time.DateTime
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class TodoEntityTest {

  companion object {
    private val FIXED_DATE_TIME = DateTime("2020-05-19T12:40:04.698")

    @JvmField
    @RegisterExtension
    val fixedTimeExtension = FixedTimeExtension(FIXED_DATE_TIME)
  }

  @Test
  fun `when create then return instance with correct fields`() {
    val title = "title"
    val description = "desc"
    val logoUrl = "logo.com"
    val item = TodoItem.create(title, description, logoUrl)

    assertEquals(TodoItem(title, description, FIXED_DATE_TIME, logoUrl), item)
  }
}