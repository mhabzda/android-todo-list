package com.mhabzda.todolist.domain.usecase

import com.mhabzda.todolist.domain.model.TodoItem
import com.mhabzda.todolist.domain.repository.TodoItemRepository
import com.mhabzda.todolist.domain.testTodoItem
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.givenBlocking

class GetTodoItemUseCaseTest {

    private val mockTodoItemRepository: TodoItemRepository = Mockito.mock()

    private val useCase = GetTodoItemUseCase(todoItemRepository = mockTodoItemRepository)

    @Test
    fun `GIVEN can get item WHEN invoke THEN return success result`() = runTest {
        val itemId = "11"
        givenBlocking { mockTodoItemRepository.getItem(itemId) }.willReturn(Result.success(testTodoItem))

        val result = useCase.invoke(itemId)

        assertEquals(Result.success(testTodoItem), result)
    }

    @Test
    fun `GIVEN cannot get item WHEN invoke THEN return failure result`() = runTest {
        val itemId = "11"
        val error = Exception()
        givenBlocking { mockTodoItemRepository.getItem(itemId) }.willReturn(Result.failure(error))

        val result = useCase.invoke(itemId)

        assertEquals(Result.failure<TodoItem>(error), result)
    }
}
