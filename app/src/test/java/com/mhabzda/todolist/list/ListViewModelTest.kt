package com.mhabzda.todolist.list

import app.cash.turbine.test
import com.mhabzda.todolist.domain.usecase.DeleteTodoItemUseCase
import com.mhabzda.todolist.domain.usecase.GetTodoItemListUseCase
import com.mhabzda.todolist.list.ListContract.ListEffect.DisplayDeletionConfirmation
import com.mhabzda.todolist.list.ListContract.ListEffect.DisplayError
import com.mhabzda.todolist.list.ListContract.ListEffect.RefreshItems
import com.mhabzda.todolist.list.ListContract.ListViewState
import com.mhabzda.todolist.util.SnackbarFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.givenBlocking
import org.mockito.kotlin.mock
import org.mockito.kotlin.verifyBlocking

class ListViewModelTest {

    private val mockGetTodoItemListUseCase: GetTodoItemListUseCase = mock()
    private val mockDeleteTodoItemListUseCase: DeleteTodoItemUseCase = mock()
    private val mockSnackbarFlow: SnackbarFlow = mock()

    private val viewModel = ListViewModel(
        getTodoItemListUseCase = mockGetTodoItemListUseCase,
        deleteTodoItemUseCase = mockDeleteTodoItemListUseCase,
        snackbarFlow = mockSnackbarFlow,
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @Test
    fun `WHEN init THEN emit paging item`() = runTest {
        viewModel.pagingFlow.test {
            awaitItem()
            ensureActive()
        }
    }

    @Test
    fun `GIVEN item can be deleted WHEN delete item THEN display confirmation and refresh items`() = runTest {
        val itemId = "as7832hads"
        givenBlocking { mockDeleteTodoItemListUseCase.invoke(itemId) }.willReturn(Result.success(Unit))

        viewModel.effects.test {
            viewModel.deleteItem(itemId)

            assertEquals(DisplayDeletionConfirmation, awaitItem())
            assertEquals(RefreshItems, awaitItem())
            assertEquals(ListViewState(showDeleteLoading = false), viewModel.state.value)
        }
    }

    @Test
    fun `GIVEN item cannot be deleted WHEN delete item THEN display error`() = runTest {
        val itemId = "as7832hads"
        val errorMessage = "Cannot delete an item"
        givenBlocking { mockDeleteTodoItemListUseCase.invoke(itemId) }.willReturn(Result.failure(Exception(errorMessage)))

        viewModel.effects.test {
            viewModel.deleteItem(itemId)

            assertEquals(DisplayError(errorMessage), awaitItem())
            assertEquals(ListViewState(showDeleteLoading = false), viewModel.state.value)
        }
    }

    @Test
    fun `WHEN show snackbar THEN emit snackbar message`() = runTest {
        val message = "message"
        viewModel.showSnackbar(message)

        verifyBlocking(mockSnackbarFlow) { emit(message) }
    }
}
