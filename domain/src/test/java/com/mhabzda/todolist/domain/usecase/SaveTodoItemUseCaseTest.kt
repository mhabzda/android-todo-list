package com.mhabzda.todolist.domain.usecase

import com.mhabzda.todolist.domain.repository.TodoItemRepository
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.givenBlocking

class SaveTodoItemUseCaseTest {

    private val mockTodoItemRepository: TodoItemRepository = Mockito.mock()

    private val useCase = SaveTodoItemUseCase(todoItemRepository = mockTodoItemRepository)

    @Test
    fun `GIVEN can save item WHEN invoke THEN return success result`() = runTest {
        val title = "Title"
        val description = "Description"
        val iconUrl = "https://icon.url"
        givenBlocking {
            mockTodoItemRepository.saveItem(title = title, description = description, iconUrl = iconUrl)
        }.willReturn(Result.success(Unit))

        val result = useCase.invoke(
            title = title,
            description = description,
            iconUrl = iconUrl,
        )

        assertEquals(Result.success(Unit), result)
    }

    @Test
    fun `GIVEN cannot save item WHEN invoke THEN return failure result`() = runTest {
        val title = "Title"
        val description = "Description"
        val iconUrl = "https://icon.url"
        val error = Exception()
        givenBlocking {
            mockTodoItemRepository.saveItem(title = title, description = description, iconUrl = iconUrl)
        }.willReturn(Result.failure(error))

        val result = useCase.invoke(title = title, description = description, iconUrl = iconUrl)

        assertEquals(Result.failure<Unit>(error), result)
    }
}