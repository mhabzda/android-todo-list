package com.mhabzda.todolist.ui.list

import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import app.cash.turbine.test
import com.todo.list.model.entities.TodoItem
import com.todo.list.model.repository.TodoRepository
import com.mhabzda.todolist.ui.TestData.testItemId
import com.mhabzda.todolist.ui.TestData.testTodoItem
import com.mhabzda.todolist.ui.list.data.ListViewEvent
import com.mhabzda.todolist.ui.list.navigation.ListRouter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.given
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@ExperimentalCoroutinesApi
class ListViewModelTest {

    private val pageSize = 10
    private val testPagingData: PagingData<TodoItem> = PagingData.from(listOf(testTodoItem))

    private val mockTodoRepository: TodoRepository = mock {
        on { fetchTodoItems(pageSize) } doReturn flowOf(testPagingData)
        on { observeItemsChanges() } doReturn emptyFlow()
        onBlocking { getItem(testItemId) } doReturn Result.success(testTodoItem)
    }
    private val router: ListRouter = mock()

    private val viewModel = ListViewModel(
        todoRepository = mockTodoRepository,
        router = router
    )

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @Test
    fun `fetch todo list when starting`() = runTest {
        verify(mockTodoRepository).fetchTodoItems(pageSize)
    }

    @Test
    fun `show loading when refresh loading is happening`() = runTest {
        viewModel.onLoadStateChange(createLoadState(refresh = LoadState.Loading))
        runCurrent()

        assertEquals(true, viewModel.state.value.isRefreshing)
    }

    @Test
    fun `show loading when append loading is happening`() = runTest {
        viewModel.onLoadStateChange(createLoadState(append = LoadState.Loading))
        runCurrent()

        assertEquals(true, viewModel.state.value.isRefreshing)
    }

    @Test
    fun `hide loading when no loading is happening`() = runTest {
        viewModel.onLoadStateChange(
            createLoadState(
                refresh = LoadState.NotLoading(false),
                append = LoadState.NotLoading(false)
            )
        )
        runCurrent()

        assertEquals(false, viewModel.state.value.isRefreshing)
    }

    @Test
    fun `display error when error occurred during refresh items loading`() = runTest {
        viewModel.onLoadStateChange(createLoadState(refresh = LoadState.Error(error)))
        runCurrent()

        viewModel.events.test { assertEquals(ListViewEvent.Error(errorMessage), awaitItem()) }
    }

    @Test
    fun `display error when error occurred during append items loading`() = runTest {
        viewModel.onLoadStateChange(createLoadState(append = LoadState.Error(error)))
        runCurrent()

        viewModel.events.test { assertEquals(ListViewEvent.Error(errorMessage), awaitItem()) }
    }

    @Test
    fun `refresh items when items have changed in the repository`() = runTest {
        given(mockTodoRepository.observeItemsChanges()).willReturn(flowOf(Unit))
        viewModel.onStart()
        runCurrent()

        viewModel.events.test { assertEquals(ListViewEvent.RefreshItems, awaitItem()) }
    }

    @Test
    fun `open item creation view when floating action button is clicked`() = runTest {
        viewModel.onFloatingButtonClick()

        verify(router).openItemCreationView()
    }

    @Test
    fun `open item open delete item confirmation dialog view when item long is clicked`() = runTest {
        viewModel.onItemLongClick(testItemId)

        verify(router).openDeleteItemConfirmationDialog(any())
    }

    @Test
    fun `display deletion confirmation message when item delete is clicked`() = runTest {
        given(router.openDeleteItemConfirmationDialog(any())).willAnswer { it.getArgument<() -> Unit>(0).invoke() }
        given(mockTodoRepository.deleteItem(testItemId)).willReturn(Result.success(Unit))

        viewModel.onItemLongClick(testItemId)

        viewModel.events.test { assertEquals(ListViewEvent.DisplayDeletionConfirmation, awaitItem()) }
    }

    @Test
    fun `display error when item delete is clicked but error occurred`() = runTest {
        val errorMessage = "Cannot delete item"
        given(router.openDeleteItemConfirmationDialog(any())).willAnswer { it.getArgument<() -> Unit>(0).invoke() }
        given(mockTodoRepository.deleteItem(testItemId)).willReturn(Result.failure(Throwable(errorMessage)))

        viewModel.onItemLongClick(testItemId)

        viewModel.events.test { assertEquals(ListViewEvent.Error(errorMessage), awaitItem()) }
    }

    @Test
    fun `open item edition view with correct data when item is clicked`() = runTest {
        viewModel.onItemClick(testItemId)

        verify(router).openItemEditionView(testItemId)
    }

    private fun createLoadState(
        refresh: LoadState = LoadState.NotLoading(false),
        append: LoadState = LoadState.NotLoading(false)
    ) = CombinedLoadStates(
        refresh = refresh,
        append = append,
        prepend = mock(),
        source = LoadStates(mock(), mock(), mock()),
        mediator = null
    )

    companion object {

        private const val errorMessage = "Cannot load item"
        private val error = Throwable(errorMessage)
    }
}
