package com.mhabzda.todolist.util

import app.cash.turbine.test
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SnackbarFlowTest {

    private val snackbarFlow = SnackbarFlow()

    @Test
    fun `WHEN emit THEN collect message from the flow`() = runTest {
        snackbarFlow.flow.test {
            val message = "message"
            snackbarFlow.emit(message)

            assertEquals(message, awaitItem())
        }
    }
}