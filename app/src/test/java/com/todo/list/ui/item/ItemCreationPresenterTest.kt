package com.todo.list.ui.item

import com.todo.list.model.repository.TodoRepository
import com.todo.list.testutilities.FixedTimeExtension
import com.todo.list.ui.TestData.testTodoItem
import com.todo.list.ui.item.creation.ItemCreationContract
import com.todo.list.ui.item.creation.ItemCreationPresenter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.joda.time.DateTime
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.mockito.kotlin.InOrderOnType
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@ExperimentalCoroutinesApi
class ItemCreationPresenterTest {

    private val view: ItemCreationContract.View = mock()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @Test
    fun `given title is empty when button clicked then display error`() = runTest {
        val presenter = createPresenter(mock())

        presenter.itemButtonClicked("", "", null)
        runCurrent()

        verify(view).displayEmptyTitleError()
    }

    @Test
    fun `given can save item when button clicked then display confirmation message and close view`() = runTest {
        val presenter = createPresenter(mock {
            onBlocking { saveItem(testItem) } doReturn Result.success(Unit)
        })

        presenter.itemButtonClicked(testItem.title, testItem.description, testItem.iconUrl)
        runCurrent()

        verify(view).displayConfirmationMessage()
        verify(view).close()
    }

    @Test
    fun `given cannot save item when button clicked then display error`() = runTest {
        val errorMessage = "Cannot save item"
        val presenter = createPresenter(mock {
            onBlocking { saveItem(testItem) } doReturn Result.failure(Throwable(errorMessage))
        })

        presenter.itemButtonClicked(testItem.title, testItem.description, testItem.iconUrl)
        runCurrent()

        verify(view).displayError(errorMessage)
    }

    @Test
    fun `given can save item when button clicked then toggle loading`() = runTest {
        val presenter = createPresenter(mock {
            onBlocking { saveItem(testItem) } doReturn Result.success(Unit)
        })

        presenter.itemButtonClicked(testItem.title, testItem.description, testItem.iconUrl)
        runCurrent()

        val inOrder = InOrderOnType(view)
        inOrder.verify(view).toggleLoading(true)
        inOrder.verify(view).toggleLoading(false)
    }

    @Test
    fun `given cannot save item when button clicked then toggle loading`() = runTest {
        val presenter = createPresenter(mock {
            onBlocking { saveItem(testItem) } doReturn Result.failure(Throwable("cannot save"))
        })

        presenter.itemButtonClicked(testItem.title, testItem.description, testItem.iconUrl)
        runCurrent()

        val inOrder = InOrderOnType(view)
        inOrder.verify(view).toggleLoading(true)
        inOrder.verify(view).toggleLoading(false)
    }

    private fun createPresenter(todoRepository: TodoRepository): ItemCreationPresenter {
        return ItemCreationPresenter(
            todoRepository = todoRepository,
            view = view
        )
    }

    companion object {
        private const val FIXED_DATE_TIME = "2020-05-19T12:40:04.698"
        private val testItem = testTodoItem.copy(creationDate = DateTime(FIXED_DATE_TIME))

        @JvmField
        @RegisterExtension
        val fixedTimeExtension = FixedTimeExtension(FIXED_DATE_TIME)
    }
}
