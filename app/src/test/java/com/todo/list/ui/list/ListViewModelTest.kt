package com.todo.list.ui.list

import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import app.cash.turbine.test
import com.todo.list.model.entities.TodoItem
import com.todo.list.model.repository.TodoRepository
import com.todo.list.testutilities.TestCoroutineExtension
import com.todo.list.ui.TestData.testTodoItem
import com.todo.list.ui.TestData.testTodoItemParcelable
import com.todo.list.ui.list.data.ListViewEvent
import com.todo.list.ui.list.navigation.ListRouter
import com.todo.list.ui.parcel.TodoItemToParcelableMapper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.given
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@ExperimentalCoroutinesApi
class ListViewModelTest {

    @RegisterExtension
    private val coroutinesExt = TestCoroutineExtension()

    private val pageSize = 10
    private val testPagingData: PagingData<TodoItem> = PagingData.from(listOf(testTodoItem))

    private val mockTodoRepository: TodoRepository = mock {
        on { fetchTodoItems(pageSize) } doReturn flowOf(testPagingData)
        on { observeItemsChanges() } doReturn emptyFlow()
    }
    private val router: ListRouter = mock()

    private val viewModel = ListViewModel(
        todoRepository = mockTodoRepository,
        router = router,
        todoItemToParcelableMapper = TodoItemToParcelableMapper()
    )

    @Test
    fun `fetch todo list when starting`() = coroutinesExt.runTest {
        viewModel.onCreate(flowOf(defaultLoadState))

        verify(mockTodoRepository).fetchTodoItems(pageSize)
    }

    @Test
    fun `show loading when refresh loading is happening`() {
        viewModel.onCreate(flowOf(createLoadState(refresh = LoadState.Loading)))

        assertEquals(true, viewModel.state.value.isRefreshing)
    }

    @Test
    fun `show loading when append loading is happening`() {
        viewModel.onCreate(flowOf(createLoadState(append = LoadState.Loading)))

        assertEquals(true, viewModel.state.value.isRefreshing)
    }

    @Test
    fun `hide loading when no loading is happening`() {
        viewModel.onCreate(
            flowOf(
                createLoadState(
                    refresh = LoadState.NotLoading(false),
                    append = LoadState.NotLoading(false)
                )
            )
        )

        assertEquals(false, viewModel.state.value.isRefreshing)
    }

    @Test
    fun `display error when error occurred during refresh items loading`() = coroutinesExt.runTest {
        val errorMessage = "Cannot load item"
        val error = Throwable(errorMessage)
        viewModel.onCreate(flowOf(createLoadState(refresh = LoadState.Error(error))))

        viewModel.events.test { assertEquals(ListViewEvent.Error(errorMessage), awaitItem()) }
    }

    @Test
    fun `display error when error occurred during append items loading`() = coroutinesExt.runTest {
        val errorMessage = "Cannot load item"
        val error = Throwable(errorMessage)
        viewModel.onCreate(flowOf(createLoadState(append = LoadState.Error(error))))

        viewModel.events.test { assertEquals(ListViewEvent.Error(errorMessage), awaitItem()) }
    }

    @Test
    fun `refresh items when items have changed in the repository`() = coroutinesExt.runTest {
        given(mockTodoRepository.observeItemsChanges()).willReturn(flowOf(Unit))
        viewModel.onCreate(flowOf())

        viewModel.events.test { assertEquals(ListViewEvent.RefreshItems, awaitItem()) }
    }

    @Test
    fun `open item creation view when floating action button is clicked`() {
        viewModel.onCreate(flowOf(defaultLoadState))

        viewModel.onFloatingButtonClick()

        verify(router).openItemCreationView()
    }

    @Test
    fun `open item open delete item confirmation dialog view when item long is clicked`() {
        viewModel.onCreate(flowOf(defaultLoadState))

        viewModel.onItemLongClick(testTodoItem)

        verify(router).openDeleteItemConfirmationDialog(any())
    }

    @Test
    fun `display deletion confirmation message when item delete is clicked`() =
        coroutinesExt.runTest {
            given(router.openDeleteItemConfirmationDialog(any())).willAnswer { it.getArgument<() -> Unit>(0).invoke() }
            given(mockTodoRepository.deleteItem(testTodoItem)).willReturn(Result.success(Unit))
            viewModel.onCreate(flowOf(defaultLoadState))

            viewModel.onItemLongClick(testTodoItem)

            viewModel.events.test { assertEquals(ListViewEvent.DisplayDeletionConfirmation, awaitItem()) }
        }

    @Test
    fun `display error when item delete is clicked but error occurred`() = coroutinesExt.runTest {
        val errorMessage = "Cannot delete item"
        given(router.openDeleteItemConfirmationDialog(any())).willAnswer { it.getArgument<() -> Unit>(0).invoke() }
        given(mockTodoRepository.deleteItem(testTodoItem)).willReturn(Result.failure(Throwable(errorMessage)))
        viewModel.onCreate(flowOf(defaultLoadState))

        viewModel.onItemLongClick(testTodoItem)

        viewModel.events.test { assertEquals(ListViewEvent.Error(errorMessage), awaitItem()) }
    }

    @Test
    fun `open item edition view with correct data when item is clicked`() {
        viewModel.onCreate(flowOf(defaultLoadState))

        viewModel.onItemClick(testTodoItem)

        verify(router).openItemEditionView(testTodoItemParcelable)
    }

    private val defaultLoadState = createLoadState(
        refresh = LoadState.Loading,
        append = LoadState.NotLoading(false)
    )

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
}
