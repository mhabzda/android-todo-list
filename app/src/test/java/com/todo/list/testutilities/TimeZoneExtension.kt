package com.todo.list.testutilities

import org.joda.time.DateTimeZone
import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext

class TimeZoneExtension(private val timezone: DateTimeZone) : BeforeAllCallback, AfterAllCallback {
    private var timeZone: DateTimeZone? = null

    override fun beforeAll(context: ExtensionContext?) {
        timeZone = DateTimeZone.getDefault()
        DateTimeZone.setDefault(timezone)
    }

    override fun afterAll(context: ExtensionContext?) {
        DateTimeZone.setDefault(timeZone)
    }
}
