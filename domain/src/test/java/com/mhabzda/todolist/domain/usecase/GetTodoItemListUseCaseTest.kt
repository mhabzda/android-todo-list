package com.mhabzda.todolist.domain.usecase

import com.mhabzda.todolist.domain.model.TodoItem
import com.mhabzda.todolist.domain.repository.TodoItemRepository
import com.mhabzda.todolist.domain.testTodoItem
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.givenBlocking

class GetTodoItemListUseCaseTest {

    private val mockTodoItemRepository: TodoItemRepository = Mockito.mock()

    private val useCase = GetTodoItemListUseCase(todoItemRepository = mockTodoItemRepository)

    @Test
    fun `GIVEN can get item list WHEN invoke THEN return success result`() = runTest {
        givenBlocking { mockTodoItemRepository.getItems(pageSize = 10, itemIdFrom = null) }.willReturn(Result.success(listOf(testTodoItem)))

        val result = useCase.invoke(pageSize = 10, itemIdFrom = null)

        assertEquals(Result.success(listOf(testTodoItem)), result)
    }

    @Test
    fun `GIVEN cannot get item list WHEN invoke THEN return failure result`() = runTest {
        val error = Exception()
        givenBlocking { mockTodoItemRepository.getItems(pageSize = 10, itemIdFrom = null) }.willReturn(Result.failure(error))

        val result = useCase.invoke(pageSize = 10, itemIdFrom = null)

        assertEquals(Result.failure<List<TodoItem>>(error), result)
    }
}