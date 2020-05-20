package com.todo.list.testutilities

import org.joda.time.DateTime
import org.joda.time.DateTimeUtils
import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext

class FixedTimeExtension(
  private val fixedTime: String
) : BeforeAllCallback, AfterAllCallback {
  override fun beforeAll(context: ExtensionContext?) {
    DateTimeUtils.setCurrentMillisFixed(DateTime(fixedTime).millis)
  }

  override fun afterAll(context: ExtensionContext?) {
    DateTimeUtils.setCurrentMillisSystem()
  }
}