package com.todo.list.ui.item

import com.nhaarman.mockitokotlin2.InOrderOnType
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.todo.list.model.repository.TodoRepository
import com.todo.list.testutilities.TestSchedulerProvider
import com.todo.list.testutilities.TimeZoneExtension
import com.todo.list.ui.TestData.testTodoItem
import com.todo.list.ui.TestData.testTodoItemParcelable
import com.todo.list.ui.item.edition.ItemEditionContract
import com.todo.list.ui.item.edition.ItemEditionPresenter
import io.reactivex.Completable
import org.joda.time.DateTimeZone
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class ItemEditionPresenterTest {
    private val view: ItemEditionContract.View = mock()
    private val testSchedulerProvider = TestSchedulerProvider()

    companion object {
        @JvmField
        @RegisterExtension
        val timezoneExtension = TimeZoneExtension(DateTimeZone.UTC)
    }

    @Test
    fun `when initializing then fill item data`() {
        val presenter = createPresenter(mock())

        presenter.runOnLifecycle {
            with(testTodoItemParcelable) {
                verify(view).fillItemData(title, description, iconUrl)
            }
        }
    }

    @Test
    fun `given title is empty when button clicked then display error`() {
        val presenter = createPresenter(mock())

        presenter.runOnLifecycle {
            itemButtonClicked("", "", null)

            verify(view).displayEmptyTitleError()
        }
    }

    @Test
    fun `given can edit item when button clicked then display confirmation message and close view`() {
        val presenter = createPresenter(mock {
            on { editItem(testTodoItem) } doReturn Completable.complete()
        })

        presenter.runOnLifecycle {
            itemButtonClicked(testTodoItem.title, testTodoItem.description, testTodoItem.iconUrl)
            testSchedulerProvider.triggerActions()

            verify(view).displayConfirmationMessage()
            verify(view).close()
        }
    }

    @Test
    fun `given cannot edit item when button clicked then display error`() {
        val errorMessage = "Cannot edit item"
        val presenter = createPresenter(mock {
            on { editItem(testTodoItem) } doReturn Completable.error(Throwable(errorMessage))
        })

        presenter.runOnLifecycle {
            itemButtonClicked(testTodoItem.title, testTodoItem.description, testTodoItem.iconUrl)
            testSchedulerProvider.triggerActions()

            verify(view).displayError(errorMessage)
        }
    }

    @Test
    fun `given can edit item when button clicked then toggle loading`() {
        val presenter = createPresenter(mock {
            on { editItem(testTodoItem) } doReturn Completable.complete()
        })

        presenter.runOnLifecycle {
            itemButtonClicked(testTodoItem.title, testTodoItem.description, testTodoItem.iconUrl)
            testSchedulerProvider.triggerActions()

            val inOrder = InOrderOnType(view)
            inOrder.verify(view).toggleLoading(true)
            inOrder.verify(view).toggleLoading(false)
        }
    }

    @Test
    fun `given cannot edit item when button clicked then toggle loading`() {
        val presenter = createPresenter(mock {
            on { editItem(testTodoItem) } doReturn Completable.error(Throwable("cannot edit"))
        })

        presenter.runOnLifecycle {
            itemButtonClicked(testTodoItem.title, testTodoItem.description, testTodoItem.iconUrl)
            testSchedulerProvider.triggerActions()

            val inOrder = InOrderOnType(view)
            inOrder.verify(view).toggleLoading(true)
            inOrder.verify(view).toggleLoading(false)
        }
    }

    private fun ItemEditionPresenter.runOnLifecycle(block: ItemEditionPresenter.() -> Unit) {
        initializeItemData()
        block(this)
        releaseResources()
    }

    private fun createPresenter(todoRepository: TodoRepository): ItemEditionPresenter {
        return ItemEditionPresenter(
            todoRepository = todoRepository,
            view = view,
            schedulerProvider = testSchedulerProvider,
            todoItemParcelable = testTodoItemParcelable
        )
    }
}
