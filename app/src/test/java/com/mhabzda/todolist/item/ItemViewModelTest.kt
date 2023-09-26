package com.mhabzda.todolist.item

//import app.cash.turbine.test
//import com.todo.list.R
//import com.todo.list.model.entities.TodoItem
//import com.todo.list.model.repository.TodoRepository
//import com.todo.list.testutilities.FixedTimeExtension
//import com.mhabzda.todolist.ui.item.data.ItemViewEvent.Close
//import com.mhabzda.todolist.ui.item.data.ItemViewEvent.DisplayMessage
//import com.mhabzda.todolist.ui.item.data.ItemViewEvent.DisplayMessageRes
//import com.mhabzda.todolist.ui.item.data.ItemViewState
//import com.mhabzda.todolist.ui.item.mapper.ItemConfirmationMessageMapper
//import com.todo.list.ui.item.mapper.ItemViewStateMapper
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import kotlinx.coroutines.test.StandardTestDispatcher
//import kotlinx.coroutines.test.TestScope
//import kotlinx.coroutines.test.runCurrent
//import kotlinx.coroutines.test.runTest
//import kotlinx.coroutines.test.setMain
//import org.joda.time.DateTime
//import org.junit.jupiter.api.Assertions.assertEquals
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.Test
//import org.junit.jupiter.api.extension.RegisterExtension
//import org.mockito.kotlin.any
//import org.mockito.kotlin.doReturn
//import org.mockito.kotlin.given
//import org.mockito.kotlin.mock
//
//@ExperimentalCoroutinesApi
//class ItemViewModelTest {
//
//    private val mockTodoRepository: TodoRepository = mock {
//        onBlocking { saveItem(any()) } doReturn Result.failure(Throwable("not initialized"))
//        onBlocking { editItem(any()) } doReturn Result.failure(Throwable("not initialized"))
//        onBlocking { getItem(existingItemId) } doReturn Result.success(testItem.copy(creationDate = existingItemId))
//    }
//
//    private val viewModel = ItemViewModel(
//        todoRepository = mockTodoRepository,
//        itemViewStateMapper = ItemViewStateMapper(),
//        itemConfirmationMessageMapper = ItemConfirmationMessageMapper()
//    )
//
//    @BeforeEach
//    fun setUp() {
//        Dispatchers.setMain(StandardTestDispatcher())
//    }
//
//    @Test
//    fun `set proper button text on start when there is a create item mode`() = runOnViewModel {
//        assertEquals(R.string.item_creation_button_title, viewModel.state.value.buttonText)
//    }
//
//    @Test
//    fun `initialize data on start when there is a edit item mode`() = runOnViewModel(existingItemId) {
//        assertEquals(
//            ItemViewState(
//                title = title,
//                description = description,
//                iconUrl = iconUrl,
//                buttonText = R.string.item_edition_button_title,
//                isLoading = false
//            ),
//            viewModel.state.value
//        )
//    }
//
//    @Test
//    fun `given title is empty when button clicked then display error`() = runOnViewModel {
//        viewModel.state.value.title = ""
//
//        viewModel.onItemButtonClick()
//        runCurrent()
//
//        viewModel.events.test { assertEquals(DisplayMessageRes(R.string.item_empty_title_message), awaitItem()) }
//    }
//
//    @Test
//    fun `given can save item when button clicked then display confirmation message and close view`() = runOnViewModel {
//        given(mockTodoRepository.saveItem(testItem)).willReturn(Result.success(Unit))
//        viewModel.provideData()
//
//        viewModel.onItemButtonClick()
//        runCurrent()
//
//        viewModel.events.test {
//            assertEquals(DisplayMessageRes(R.string.item_creation_confirmation_message), awaitItem())
//            assertEquals(Close, awaitItem())
//        }
//        assertEquals(false, viewModel.state.value.isLoading)
//    }
//
//    @Test
//    fun `given cannot save item when button clicked then display error`() = runOnViewModel {
//        val errorMessage = "Cannot save item"
//        given(mockTodoRepository.saveItem(testItem)).willReturn(Result.failure(Throwable(errorMessage)))
//        viewModel.provideData()
//
//        viewModel.onItemButtonClick()
//        runCurrent()
//
//        viewModel.events.test { assertEquals(DisplayMessage(errorMessage), awaitItem()) }
//        runCurrent()
//        assertEquals(false, viewModel.state.value.isLoading)
//    }
//
//    @Test
//    fun `given can edit item when button clicked then display confirmation message and close view`() =
//        runOnViewModel(existingItemId) {
//            given(mockTodoRepository.editItem(testItem.copy(creationDate = existingItemId)))
//                .willReturn(Result.success(Unit))
//
//            viewModel.onItemButtonClick()
//            runCurrent()
//
//            viewModel.events.test {
//                assertEquals(DisplayMessageRes(R.string.item_edition_confirmation_message), awaitItem())
//                assertEquals(Close, awaitItem())
//            }
//            assertEquals(false, viewModel.state.value.isLoading)
//        }
//
//    @Test
//    fun `given cannot edit item when button clicked then display error`() = runOnViewModel(existingItemId) {
//        val errorMessage = "Cannot edit item"
//        given(mockTodoRepository.editItem(testItem.copy(creationDate = existingItemId)))
//            .willReturn(Result.failure(Throwable(errorMessage)))
//
//        viewModel.onItemButtonClick()
//        runCurrent()
//
//        viewModel.events.test { assertEquals(DisplayMessage(errorMessage), awaitItem()) }
//        runCurrent()
//        assertEquals(false, viewModel.state.value.isLoading)
//    }
//
//    private fun runOnViewModel(
//        itemId: DateTime? = null,
//        testBody: suspend TestScope.() -> Unit
//    ) = runTest {
//        viewModel.onStart(itemId)
//        runCurrent()
//        testBody.invoke(this)
//    }
//
//    private fun ItemViewModel.provideData() {
//        state.value.title = testItem.title
//        state.value.description = testItem.description
//        state.value.iconUrl = testItem.iconUrl
//    }
//
//    companion object {
//        private const val title = "title"
//        private const val description = "desc"
//        private const val iconUrl = "logo.com"
//
//        private const val currentTime = "2022-02-25T12:40:04.698"
//        private val currentItemId = DateTime(currentTime)
//        private const val existingItemTime = "2022-02-23T12:40:04.698"
//        private val existingItemId = DateTime(existingItemTime)
//
//        private val testItem = TodoItem(
//            title = title,
//            description = description,
//            creationDate = currentItemId,
//            iconUrl = iconUrl
//        )
//
//        @JvmField
//        @RegisterExtension
//        val fixedTimeExtension = FixedTimeExtension(currentTime)
//    }
//}
