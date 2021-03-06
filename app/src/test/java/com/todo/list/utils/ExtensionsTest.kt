package com.todo.list.utils

import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ExtensionsTest {
  @Test
  fun `when format date time then return text with day month year hour and minute`() {
    val timezone = DateTimeZone.getDefault()
    DateTimeZone.setDefault(DateTimeZone.UTC)

    val dateTime = DateTime("2020-05-19T12:40:04.698Z")
    val result = dateTime.formatDateHour()

    assertEquals("19.05.2020 12:40", result)

    DateTimeZone.setDefault(timezone)
  }

  @Test
  fun `given null value when isNotNull check then return false`() {
    val value: Any? = null

    val result = value.isNotNull()

    assertEquals(false, result)
  }

  @Test
  fun `given non-null value when isNotNull check then return true`() {
    val value: Any? = Any()

    val result = value.isNotNull()

    assertEquals(true, result)
  }
}