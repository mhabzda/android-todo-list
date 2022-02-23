package com.todo.list.ui.item

import com.todo.list.model.repository.TodoRepository
import com.todo.list.testutilities.TestCoroutineExtension
import com.todo.list.testutilities.TimeZoneExtension
import com.todo.list.ui.TestData.testTodoItem
import com.todo.list.ui.TestData.testTodoItemParcelable
import com.todo.list.ui.item.edition.ItemEditionContract
import com.todo.list.ui.item.edition.ItemEditionPresenter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.joda.time.DateTimeZone
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.mockito.kotlin.InOrderOnType
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@ExperimentalCoroutinesApi
class ItemEditionPresenterTest {
    private val view: ItemEditionContract.View = mock()

    @RegisterExtension
    private val coroutinesExt = TestCoroutineExtension()

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
            onBlocking { editItem(testTodoItem) } doReturn Result.success(Unit)
        })

        presenter.runOnLifecycle {
            itemButtonClicked(testTodoItem.title, testTodoItem.description, testTodoItem.iconUrl)

            verify(view).displayConfirmationMessage()
            verify(view).close()
        }
    }

    @Test
    fun `given cannot edit item when button clicked then display error`() {
        val errorMessage = "Cannot edit item"
        val presenter = createPresenter(mock {
            onBlocking { editItem(testTodoItem) } doReturn Result.failure(Throwable(errorMessage))
        })

        presenter.runOnLifecycle {
            itemButtonClicked(testTodoItem.title, testTodoItem.description, testTodoItem.iconUrl)

            verify(view).displayError(errorMessage)
        }
    }

    @Test
    fun `given can edit item when button clicked then toggle loading`() {
        val presenter = createPresenter(mock {
            onBlocking { editItem(testTodoItem) } doReturn Result.success(Unit)
        })

        presenter.runOnLifecycle {
            itemButtonClicked(testTodoItem.title, testTodoItem.description, testTodoItem.iconUrl)

            val inOrder = InOrderOnType(view)
            inOrder.verify(view).toggleLoading(true)
            inOrder.verify(view).toggleLoading(false)
        }
    }

    @Test
    fun `given cannot edit item when button clicked then toggle loading`() {
        val presenter = createPresenter(mock {
            onBlocking { editItem(testTodoItem) } doReturn Result.failure(Throwable("cannot edit"))
        })

        presenter.runOnLifecycle {
            itemButtonClicked(testTodoItem.title, testTodoItem.description, testTodoItem.iconUrl)

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
            todoItemParcelable = testTodoItemParcelable
        )
    }
}
