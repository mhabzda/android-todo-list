package com.todo.list.ui.item

import app.cash.turbine.test
import com.todo.list.R
import com.todo.list.model.repository.TodoRepository
import com.todo.list.testutilities.FixedTimeExtension
import com.todo.list.ui.TestData.testTodoItem
import com.todo.list.ui.itemcreation.ItemCreationViewModel
import com.todo.list.ui.itemcreation.data.ItemCreationViewEvent.Close
import com.todo.list.ui.itemcreation.data.ItemCreationViewEvent.DisplayMessage
import com.todo.list.ui.itemcreation.data.ItemCreationViewEvent.DisplayMessageRes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.joda.time.DateTime
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.mockito.kotlin.given
import org.mockito.kotlin.mock

@ExperimentalCoroutinesApi
class ItemCreationViewModelTest {

    private val mockTodoRepository: TodoRepository = mock()

    private val viewModel = ItemCreationViewModel(
        todoRepository = mockTodoRepository
    )

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @Test
    fun `given title is empty when button clicked then display error`() = runTest {
        viewModel.onTitleChange("")

        viewModel.onItemButtonClick()
        runCurrent()

        viewModel.events.test { assertEquals(DisplayMessageRes(R.string.item_empty_title_message), awaitItem()) }
    }

    @Test
    fun `given can save item when button clicked then display confirmation message and close view`() = runTest {
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
    fun `given cannot save item when button clicked then display error`() = runTest {
        val errorMessage = "Cannot save item"
        given(mockTodoRepository.saveItem(testItem)).willReturn(Result.failure(Throwable(errorMessage)))
        viewModel.provideData()

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

    private fun ItemCreationViewModel.provideData() {
        onTitleChange(testItem.title)
        onDescriptionChange(testItem.description)
        onIconUrlChange(testItem.iconUrl ?: "")
    }

    companion object {
        private const val FIXED_DATE_TIME = "2020-05-19T12:40:04.698"
        private val testItem = testTodoItem.copy(creationDate = DateTime(FIXED_DATE_TIME))

        @JvmField
        @RegisterExtension
        val fixedTimeExtension = FixedTimeExtension(FIXED_DATE_TIME)
    }
}
