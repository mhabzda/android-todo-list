package com.mhabzda.todolist.utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.inOrder
import org.mockito.kotlin.mock
import java.time.ZonedDateTime

class ExtensionsTest {
    @Test
    fun `WHEN format date time THEN return text with day month year hour and minute`() {
        val givenDateTime = ZonedDateTime.parse("2023-09-13T17:23:34.000000234+02:00[Europe/Paris]")

        val result = givenDateTime.format()

        assertEquals("13.09.2023 17:23", result)
    }

    @Test
    fun `GIVEN result is success WHEN invoke THEN trigger success and terminate blocks`() {
        val successBlock: () -> Unit = mock()
        val terminateBlock: () -> Unit = mock()
        Result.success(Unit)
            .onSuccess { successBlock.invoke() }
            .onTerminate { terminateBlock.invoke() }

        val inOrder = inOrder(successBlock, terminateBlock)
        inOrder.verify(successBlock).invoke()
        inOrder.verify(terminateBlock).invoke()
    }

    @Test
    fun `GIVEN result is failure WHEN invoke THEN trigger success and terminate blocks`() {
        val failureBlock: () -> Unit = mock()
        val terminateBlock: () -> Unit = mock()
        Result.failure<Unit>(Throwable())
            .onFailure { failureBlock.invoke() }
            .onTerminate { terminateBlock.invoke() }

        val inOrder = inOrder(failureBlock, terminateBlock)
        inOrder.verify(failureBlock).invoke()
        inOrder.verify(terminateBlock).invoke()
    }
}
