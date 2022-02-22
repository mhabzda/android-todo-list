package com.todo.list.ui.list

import androidx.paging.PagedList
import com.nhaarman.mockitokotlin2.InOrderOnType
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.clearInvocations
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.todo.list.model.entities.TodoItem
import com.todo.list.model.repository.TodoRepository
import com.todo.list.model.repository.model.NetworkState
import com.todo.list.model.repository.model.PagingObservable
import com.todo.list.testutilities.TestSchedulerProvider
import com.todo.list.ui.TestData.testTodoItem
import com.todo.list.ui.TestData.testTodoItemParcelable
import com.todo.list.ui.parcel.TodoItemToParcelableMapper
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import org.junit.jupiter.api.Test

class ListPresenterTest {
    private val pageSize = 30
    private val testPagedList: PagedList<TodoItem> = mock()
    private val defaultTodoRepositoryMock: TodoRepository = mock {
        on { fetchTodoItems(pageSize) } doReturn
            PagingObservable(Observable.just(testPagedList), Observable.just(NetworkState.Loading, NetworkState.Loaded))
        on { observeItemsChanges() } doReturn Observable.never()
    }

    private val testSchedulerProvider = TestSchedulerProvider()
    private val view: ListContract.View = mock()

    @Test
    fun `given can fetch items when observe paged data then display todo list`() {
        val presenter = createPresenter(
            todoRepository = mock {
                on { fetchTodoItems(pageSize) } doReturn PagingObservable(
                    Observable.just(testPagedList),
                    Observable.never()
                )
                on { observeItemsChanges() } doReturn Observable.never()
            }
        )

        presenter.runOnLifecycle {
            testSchedulerProvider.triggerActions()

            verify(view).displayTodoList(testPagedList)
        }
    }

    @Test
    fun `given can fetch items when observe paged data then toggle loading`() {
        val presenter = createPresenter(
            todoRepository = mock {
                on { fetchTodoItems(pageSize) } doReturn
                    PagingObservable(
                        Observable.just(testPagedList),
                        Observable.just(NetworkState.Loading, NetworkState.Loaded)
                    )
                on { observeItemsChanges() } doReturn Observable.never()
            }
        )

        presenter.runOnLifecycle {
            testSchedulerProvider.triggerActions()

            val inOrder = InOrderOnType(view)
            inOrder.verify(view).setRefreshingState(true)
            inOrder.verify(view).setRefreshingState(false)
        }
    }

    @Test
    fun `given error while fetching items when observe paged data then hide loading and display error`() {
        val errorMessage = "Cannot load item"
        val error = Throwable(errorMessage)
        val presenter = createPresenter(
            todoRepository = mock {
                on { fetchTodoItems(pageSize) } doReturn
                    PagingObservable(
                        Observable.never(),
                        Observable.just(NetworkState.Loading, NetworkState.Error(error))
                    )
                on { observeItemsChanges() } doReturn Observable.never()
            }
        )

        presenter.runOnLifecycle {
            testSchedulerProvider.triggerActions()

            verify(view).setRefreshingState(false)
            verify(view).displayError(errorMessage)
        }
    }

    @Test
    fun `given observing paged data when items changes then refresh items`() {
        val todoRepository: TodoRepository = mock {
            on { fetchTodoItems(pageSize) } doReturn PagingObservable(
                Observable.just(testPagedList),
                Observable.never()
            )
            on { observeItemsChanges() } doReturn Observable.just(Any())
        }
        val presenter = createPresenter(todoRepository)

        presenter.runOnLifecycle {
            testSchedulerProvider.triggerActions()

            verify(todoRepository).refreshTodoItems()
        }
    }

    @Test
    fun `when refresh items then refresh items in repository`() {
        val presenter = createPresenter()

        presenter.runOnLifecycle {
            testSchedulerProvider.triggerActions()
            refreshItems()

            verify(defaultTodoRepositoryMock).refreshTodoItems()
        }
    }

    @Test
    fun `when floating action button clicked then open item creation view`() {
        val router: ListContract.Router = mock()
        val presenter = createPresenter(router = router)

        presenter.runOnLifecycle {
            testSchedulerProvider.triggerActions()
            floatingButtonClicked()

            verify(router).openItemCreationView()
        }
    }

    @Test
    fun `when item long clicked then open item open delete item confirmation dialog view`() {
        val router: ListContract.Router = mock()
        val presenter = createPresenter(router = router)

        presenter.runOnLifecycle {
            testSchedulerProvider.triggerActions()
            itemLongClicked(testTodoItem)

            verify(router).openDeleteItemConfirmationDialog(any())
        }
    }

    @Test
    fun `given can delete item when item delete clicked then display deletion confirmation message and toggle loading`() {
        val router: ListContract.Router = mock {
            on { openDeleteItemConfirmationDialog(any()) } doAnswer { it.getArgument<() -> Unit>(0).invoke() }
        }
        whenever(defaultTodoRepositoryMock.deleteItem(testTodoItem)).thenReturn(Completable.complete())
        val presenter = createPresenter(router = router)

        presenter.runOnLifecycle {
            testSchedulerProvider.triggerActions()
            clearInvocations(view)

            itemLongClicked(testTodoItem)
            testSchedulerProvider.triggerActions()

            verify(view).setRefreshingState(true)
            verify(view).displayItemDeletionConfirmationMessage()
            verify(view).setRefreshingState(false)
        }
    }

    @Test
    fun `given cannot delete item when item delete clicked then display error and toggle loading`() {
        val errorMessage = "Cannot delete item"
        val router: ListContract.Router = mock {
            on { openDeleteItemConfirmationDialog(any()) } doAnswer { it.getArgument<() -> Unit>(0).invoke() }
        }
        whenever(defaultTodoRepositoryMock.deleteItem(testTodoItem)).thenReturn(Completable.error(Throwable(errorMessage)))
        val presenter = createPresenter(router = router)

        presenter.runOnLifecycle {
            testSchedulerProvider.triggerActions()
            clearInvocations(view)

            itemLongClicked(testTodoItem)
            testSchedulerProvider.triggerActions()

            verify(view).setRefreshingState(true)
            verify(view).displayError(errorMessage)
            verify(view).setRefreshingState(false)
        }
    }

    @Test
    fun `when item clicked then open item edition view with correct data`() {
        val router: ListContract.Router = mock()
        val presenter = createPresenter(router = router)

        presenter.runOnLifecycle {
            testSchedulerProvider.triggerActions()
            itemClicked(testTodoItem)

            verify(router).openItemEditionView(testTodoItemParcelable)
        }
    }

    @Test
    fun `given lifecycle closed when repository emits an item then ignore items emitted after close`() {
        val itemsSubject = PublishSubject.create<PagedList<TodoItem>>()
        val presenter = createPresenter(
            todoRepository = mock {
                on { fetchTodoItems(pageSize) } doReturn
                    PagingObservable(itemsSubject.hide(), Observable.just(NetworkState.Loading, NetworkState.Loaded))
                on { observeItemsChanges() } doReturn Observable.never()
            }
        )

        presenter.runOnLifecycle {
            itemsSubject.onNext(testPagedList)
            testSchedulerProvider.triggerActions()
        }
        itemsSubject.onNext(testPagedList)
        testSchedulerProvider.triggerActions()

        verify(view, times(1)).displayTodoList(testPagedList)
    }

    private fun ListPresenter.runOnLifecycle(block: ListPresenter.() -> Unit) {
        observePagedData()
        block(this)
        clearResources()
    }

    private fun createPresenter(
        todoRepository: TodoRepository = this.defaultTodoRepositoryMock,
        router: ListContract.Router = mock()
    ): ListPresenter {
        return ListPresenter(
            todoRepository = todoRepository,
            schedulerProvider = testSchedulerProvider,
            view = view,
            router = router,
            todoItemToParcelableMapper = TodoItemToParcelableMapper()
        )
    }
}
