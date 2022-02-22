package com.todo.list.ui.item

import com.todo.list.model.repository.TodoRepository
import com.todo.list.testutilities.FixedTimeExtension
import com.todo.list.testutilities.TestSchedulerProvider
import com.todo.list.ui.TestData.testTodoItem
import com.todo.list.ui.item.creation.ItemCreationContract
import com.todo.list.ui.item.creation.ItemCreationPresenter
import io.reactivex.Completable
import org.joda.time.DateTime
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.mockito.kotlin.InOrderOnType
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class ItemCreationPresenterTest {
    private val view: ItemCreationContract.View = mock()
    private val testSchedulerProvider = TestSchedulerProvider()

    companion object {
        private const val FIXED_DATE_TIME = "2020-05-19T12:40:04.698"
        private val testItem = testTodoItem.copy(creationDate = DateTime(FIXED_DATE_TIME))

        @JvmField
        @RegisterExtension
        val fixedTimeExtension = FixedTimeExtension(FIXED_DATE_TIME)
    }

    @Test
    fun `given title is empty when button clicked then display error`() {
        val presenter = createPresenter(mock())

        presenter.itemButtonClicked("", "", null)

        verify(view).displayEmptyTitleError()
    }

    @Test
    fun `given can save item when button clicked then display confirmation message and close view`() {
        val presenter = createPresenter(mock {
            on { saveItem(testItem) } doReturn Completable.complete()
        })

        presenter.itemButtonClicked(testItem.title, testItem.description, testItem.iconUrl)
        testSchedulerProvider.triggerActions()

        verify(view).displayConfirmationMessage()
        verify(view).close()
    }

    @Test
    fun `given cannot save item when button clicked then display error`() {
        val errorMessage = "Cannot save item"
        val presenter = createPresenter(mock {
            on { saveItem(testItem) } doReturn Completable.error(Throwable(errorMessage))
        })

        presenter.itemButtonClicked(testItem.title, testItem.description, testItem.iconUrl)
        testSchedulerProvider.triggerActions()

        verify(view).displayError(errorMessage)
    }

    @Test
    fun `given can save item when button clicked then toggle loading`() {
        val presenter = createPresenter(mock {
            on { saveItem(testItem) } doReturn Completable.complete()
        })

        presenter.itemButtonClicked(testItem.title, testItem.description, testItem.iconUrl)
        testSchedulerProvider.triggerActions()

        val inOrder = InOrderOnType(view)
        inOrder.verify(view).toggleLoading(true)
        inOrder.verify(view).toggleLoading(false)
    }

    @Test
    fun `given cannot save item when button clicked then toggle loading`() {
        val presenter = createPresenter(mock {
            on { saveItem(testItem) } doReturn Completable.error(Throwable("cannot save"))
        })

        presenter.itemButtonClicked(testItem.title, testItem.description, testItem.iconUrl)
        testSchedulerProvider.triggerActions()

        val inOrder = InOrderOnType(view)
        inOrder.verify(view).toggleLoading(true)
        inOrder.verify(view).toggleLoading(false)
    }

    private fun createPresenter(todoRepository: TodoRepository): ItemCreationPresenter {
        return ItemCreationPresenter(
            todoRepository = todoRepository,
            view = view,
            schedulerProvider = testSchedulerProvider
        )
    }
}
