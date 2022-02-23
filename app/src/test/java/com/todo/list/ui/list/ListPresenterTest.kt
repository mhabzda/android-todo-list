package com.todo.list.ui.list

import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import com.todo.list.model.entities.TodoItem
import com.todo.list.model.repository.TodoRepository
import com.todo.list.testutilities.TestCoroutineExtension
import com.todo.list.testutilities.TestSchedulerProvider
import com.todo.list.ui.TestData.testTodoItem
import com.todo.list.ui.TestData.testTodoItemParcelable
import com.todo.list.ui.parcel.TodoItemToParcelableMapper
import io.reactivex.Completable
import io.reactivex.Observable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.clearInvocations
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.given
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@ExperimentalCoroutinesApi
class ListPresenterTest {

    @RegisterExtension
    private val coroutinesExt = TestCoroutineExtension()

    private val pageSize = 10
    private val testPagingData: PagingData<TodoItem> = PagingData.from(listOf(testTodoItem))

    private val mockTodoRepository: TodoRepository = mock {
        on { fetchTodoItems(pageSize) } doReturn flowOf(testPagingData)
        on { observeItemsChanges() } doReturn Observable.never()
    }
    private val testSchedulerProvider = TestSchedulerProvider()
    private val view: ListContract.View = mock()
    private val router: ListContract.Router = mock()

    private val presenter = ListPresenter(
        todoRepository = mockTodoRepository,
        schedulerProvider = testSchedulerProvider,
        view = view,
        router = router,
        todoItemToParcelableMapper = TodoItemToParcelableMapper()
    )

    @Test
    fun `display todo list when starting`() = coroutinesExt.runTest {
        presenter.onStart(flowOf(defaultLoadState))

        verify(view).displayTodoList(testPagingData)
    }

    @Test
    fun `show loading when refresh loading is happening`() {
        presenter.onStart(flowOf(createLoadState(refresh = LoadState.Loading)))

        verify(view).setRefreshingState(true)
    }

    @Test
    fun `show loading when append loading is happening`() {
        presenter.onStart(flowOf(createLoadState(append = LoadState.Loading)))
        verify(view).setRefreshingState(true)
    }

    @Test
    fun `hide loading when no loading is happening`() {
        presenter.onStart(
            flowOf(
                createLoadState(
                    refresh = LoadState.NotLoading(false),
                    append = LoadState.NotLoading(false)
                )
            )
        )
        verify(view).setRefreshingState(false)
    }

    @Test
    fun `display error when error occurred during refresh items loading`() {
        val errorMessage = "Cannot load item"
        val error = Throwable(errorMessage)
        presenter.onStart(flowOf(createLoadState(refresh = LoadState.Error(error))))

        verify(view).displayError(errorMessage)
    }

    @Test
    fun `display error when error occurred during append items loading`() {
        val errorMessage = "Cannot load item"
        val error = Throwable(errorMessage)
        presenter.onStart(flowOf(createLoadState(append = LoadState.Error(error))))

        verify(view).displayError(errorMessage)
    }

    @Test
    fun `refresh items when items have changed in the repository`() {
        given(mockTodoRepository.observeItemsChanges()).willReturn(Observable.just(Any()))
        presenter.onStart(flowOf())

        testSchedulerProvider.triggerActions()

        verify(view).refreshListItems()
    }

    @Test
    fun `open item creation view when floating action button is clicked`() {
        presenter.onStart(flowOf(defaultLoadState))

        presenter.floatingButtonClicked()

        verify(router).openItemCreationView()
    }

    @Test
    fun `open item open delete item confirmation dialog view when item long is clicked`() {
        presenter.onStart(flowOf(defaultLoadState))

        presenter.itemLongClicked(testTodoItem)

        verify(router).openDeleteItemConfirmationDialog(any())
    }

    @Test
    fun `display deletion confirmation message and toggle loading when item delete is clicked`() {
        given(router.openDeleteItemConfirmationDialog(any())).willAnswer { it.getArgument<() -> Unit>(0).invoke() }
        given(mockTodoRepository.deleteItem(testTodoItem)).willReturn(Completable.complete())
        presenter.onStart(flowOf(defaultLoadState))

        clearInvocations(view)

        presenter.itemLongClicked(testTodoItem)
        testSchedulerProvider.triggerActions()

        verify(view).setRefreshingState(true)
        verify(view).displayItemDeletionConfirmationMessage()
        verify(view).setRefreshingState(false)
    }

    @Test
    fun `display error and toggle loading when item delete is clicked but error occurred`() {
        val errorMessage = "Cannot delete item"
        given(router.openDeleteItemConfirmationDialog(any())).willAnswer { it.getArgument<() -> Unit>(0).invoke() }
        given(mockTodoRepository.deleteItem(testTodoItem)).willReturn(Completable.error(Throwable(errorMessage)))
        presenter.onStart(flowOf(defaultLoadState))

        clearInvocations(view)

        presenter.itemLongClicked(testTodoItem)
        testSchedulerProvider.triggerActions()

        verify(view).setRefreshingState(true)
        verify(view).displayError(errorMessage)
        verify(view).setRefreshingState(false)
    }

    @Test
    fun `open item edition view with correct data when item is clicked`() {
        presenter.onStart(flowOf(defaultLoadState))

        presenter.itemClicked(testTodoItem)

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
