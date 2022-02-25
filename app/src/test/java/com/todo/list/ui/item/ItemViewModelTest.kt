package com.todo.list.ui.item

import app.cash.turbine.test
import com.todo.list.R
import com.todo.list.model.entities.TodoItem
import com.todo.list.model.repository.TodoRepository
import com.todo.list.testutilities.FixedTimeExtension
import com.todo.list.ui.item.data.ItemViewEvent.Close
import com.todo.list.ui.item.data.ItemViewEvent.DisplayMessage
import com.todo.list.ui.item.data.ItemViewEvent.DisplayMessageRes
import com.todo.list.ui.item.data.ItemViewState
import com.todo.list.ui.item.mapper.ItemConfirmationMessageMapper
import com.todo.list.ui.item.mapper.ItemViewStateMapper
import com.todo.list.ui.parcel.TodoItemParcelable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.joda.time.DateTime
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.given
import org.mockito.kotlin.mock

@ExperimentalCoroutinesApi
class ItemViewModelTest {

    private val mockTodoRepository: TodoRepository = mock {
        onBlocking { saveItem(any()) } doReturn Result.failure(Throwable("not initialized"))
        onBlocking { editItem(any()) } doReturn Result.failure(Throwable("not initialized"))
    }

    private val viewModel = ItemViewModel(
        todoRepository = mockTodoRepository,
        itemViewStateMapper = ItemViewStateMapper(),
        itemConfirmationMessageMapper = ItemConfirmationMessageMapper()
    )

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @Test
    fun `set proper button text on start when there is a create item mode`() = runOnViewModel {
        assertEquals(R.string.item_creation_button_title, viewModel.state.value.buttonText)
    }

    @Test
    fun `initialize data on start when there is a edit item mode`() = runOnViewModel(testItemParcelable) {
        assertEquals(
            ItemViewState(
                title = title,
                description = description,
                iconUrl = iconUrl,
                buttonText = R.string.item_edition_button_title,
                isLoading = false
            ),
            viewModel.state.value
        )
    }

    @Test
    fun `given title is empty when button clicked then display error`() = runOnViewModel {
        viewModel.onTitleChange("")

        viewModel.onItemButtonClick()
        runCurrent()

        viewModel.events.test { assertEquals(DisplayMessageRes(R.string.item_empty_title_message), awaitItem()) }
    }

    @Test
    fun `given can save item when button clicked then display confirmation message and close view`() = runOnViewModel {
        given(mockTodoRepository.saveItem(testItem)).willReturn(Result.success(Unit))
        viewModel.provideData()

        viewModel.onItemButtonClick()
        runCurrent()

        viewModel.events.test {
            assertEquals(DisplayMessageRes(R.string.item_creation_confirmation_message), awaitItem())
            assertEquals(Close, awaitItem())
        }
        assertEquals(false, viewModel.state.value.isLoading)
    }

    @Test
    fun `given cannot save item when button clicked then display error`() = runOnViewModel {
        val errorMessage = "Cannot save item"
        given(mockTodoRepository.saveItem(testItem)).willReturn(Result.failure(Throwable(errorMessage)))
        viewModel.provideData()

        viewModel.onItemButtonClick()
        runCurrent()

        viewModel.events.test { assertEquals(DisplayMessage(errorMessage), awaitItem()) }
        assertEquals(false, viewModel.state.value.isLoading)
    }

    @Test
    fun `given can edit item when button clicked then display confirmation message and close view`() =
        runOnViewModel(testItemParcelable) {
            given(mockTodoRepository.editItem(testItem.copy(creationDate = DateTime(existingItemTime))))
                .willReturn(Result.success(Unit))

            viewModel.onItemButtonClick()
            runCurrent()

            viewModel.events.test {
                assertEquals(DisplayMessageRes(R.string.item_edition_confirmation_message), awaitItem())
                assertEquals(Close, awaitItem())
            }
            assertEquals(false, viewModel.state.value.isLoading)
        }

    @Test
    fun `given cannot edit item when button clicked then display error`() = runOnViewModel(testItemParcelable) {
        val errorMessage = "Cannot edit item"
        given(mockTodoRepository.editItem(testItem.copy(creationDate = DateTime(existingItemTime))))
            .willReturn(Result.failure(Throwable(errorMessage)))

        viewModel.onItemButtonClick()
        runCurrent()

        viewModel.events.test { assertEquals(DisplayMessage(errorMessage), awaitItem()) }
        assertEquals(false, viewModel.state.value.isLoading)
    }

    @Test
    fun `update state when title is changed`() {
        viewModel.onTitleChange("title")

        assertEquals("title", viewModel.state.value.title)
    }

    @Test
    fun `update state when description is changed`() {
        viewModel.onDescriptionChange("description")

        assertEquals("description", viewModel.state.value.description)
    }

    @Test
    fun `update state when icon url is changed`() {
        viewModel.onIconUrlChange("iconUrl")

        assertEquals("iconUrl", viewModel.state.value.iconUrl)
    }

    private fun runOnViewModel(
        todoItemParcelable: TodoItemParcelable? = null,
        testBody: suspend TestScope.() -> Unit
    ) = runTest {
        viewModel.onCreate(todoItemParcelable)
        testBody.invoke(this)
    }

    private fun ItemViewModel.provideData() {
        onTitleChange(testItem.title)
        onDescriptionChange(testItem.description)
        onIconUrlChange(testItem.iconUrl ?: "")
    }

    companion object {
        private const val title = "title"
        private const val description = "desc"
        private const val iconUrl = "logo.com"

        private const val currentTime = "2022-02-25T12:40:04.698"
        private const val existingItemTime = "2022-02-23T12:40:04.698"

        private val testItem = TodoItem(
            title = title,
            description = description,
            creationDate = DateTime(currentTime),
            iconUrl = iconUrl
        )

        private val testItemParcelable = TodoItemParcelable(
            title = title,
            description = description,
            creationDate = existingItemTime,
            iconUrl = iconUrl
        )

        @JvmField
        @RegisterExtension
        val fixedTimeExtension = FixedTimeExtension(currentTime)
    }
}
