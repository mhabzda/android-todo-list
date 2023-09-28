package com.mhabzda.todolist.domain.usecase

import com.mhabzda.todolist.domain.repository.TodoItemRepository
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.givenBlocking

class DeleteTodoItemUseCaseTest {

    private val mockTodoItemRepository: TodoItemRepository = mock()

    private val useCase = DeleteTodoItemUseCase(todoItemRepository = mockTodoItemRepository)

    @Test
    fun `GIVEN can delete item WHEN invoke THEN return success result`() = runTest {
        val itemId = "iaud671dass"
        givenBlocking { mockTodoItemRepository.deleteItem(itemId) }.willReturn(Result.success(Unit))

        val result = useCase.invoke(itemId)

        assertEquals(Result.success(Unit), result)
    }

    @Test
    fun `GIVEN cannot delete item WHEN invoke THEN return failure result`() = runTest {
        val itemId = "iaud671dass"
        val error = Exception()
        givenBlocking { mockTodoItemRepository.deleteItem(itemId) }.willReturn(Result.failure(error))

        val result = useCase.invoke(itemId)

        assertEquals(Result.failure<Unit>(error), result)
    }
}
