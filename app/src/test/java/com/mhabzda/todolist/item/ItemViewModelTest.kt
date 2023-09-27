package com.mhabzda.todolist.item

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.mhabzda.todolist.R
import com.mhabzda.todolist.domain.model.TodoItem
import com.mhabzda.todolist.domain.usecase.CreateTodoItemUseCase
import com.mhabzda.todolist.domain.usecase.EditTodoItemUseCase
import com.mhabzda.todolist.domain.usecase.GetTodoItemUseCase
import com.mhabzda.todolist.item.ItemContract.ItemEffect.Close
import com.mhabzda.todolist.item.ItemContract.ItemEffect.DisplayMessage
import com.mhabzda.todolist.item.ItemContract.ItemEffect.DisplayMessageRes
import com.mhabzda.todolist.item.ItemContract.ItemEffect.DisplayTitleError
import com.mhabzda.todolist.item.ItemContract.ItemEffect.InitDescription
import com.mhabzda.todolist.item.ItemContract.ItemEffect.InitIconUrl
import com.mhabzda.todolist.item.ItemContract.ItemEffect.InitTitle
import com.mhabzda.todolist.item.ItemContract.ItemViewState
import com.mhabzda.todolist.item.mapper.ItemConfirmationMessageMapper
import com.mhabzda.todolist.util.SnackbarFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.given
import org.mockito.kotlin.givenBlocking
import org.mockito.kotlin.mock
import org.mockito.kotlin.verifyBlocking
import java.time.ZonedDateTime

class ItemViewModelTest {

    private val itemIdKey = "itemId"

    private val itemId = "akdlfj24903jfd"
    private val title = "title"
    private val description = "description"
    private val iconUrl = "logo.com"
    private val testTodoItem = TodoItem(
        id = itemId,
        title = title,
        description = description,
        creationDateTime = ZonedDateTime.parse("2023-09-13T17:23:34.000000234+02:00[Europe/Paris]"),
        iconUrl = iconUrl,
    )

    private val testDispatcher = StandardTestDispatcher()

    private val mockSavedStateHandle: SavedStateHandle = mock {
        on { get<String?>(itemIdKey) } doReturn null
    }
    private val mockGetTodoItemUseCase: GetTodoItemUseCase = mock {
        onBlocking { invoke(itemId) } doReturn Result.success(testTodoItem)
    }
    private val mockCreateTodoItemUseCase: CreateTodoItemUseCase = mock()
    private val mockEditTodoItemUseCase: EditTodoItemUseCase = mock()
    private val mockSnackbarFlow: SnackbarFlow = mock()

    @OptIn(ExperimentalCoroutinesApi::class)
    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @Test
    fun `GIVEN create item mode WHEN init THEN set proper button text`() {
        given { mockSavedStateHandle.get<String?>(itemIdKey) }.willReturn(null)
        val viewModel = createViewModel()

        assertEquals(
            ItemViewState(
                buttonText = R.string.item_create_button_title,
                isLoading = false,
            ),
            viewModel.state.value,
        )
    }

    @Test
    fun `GIVEN edit item mode WHEN init THEN initialize data`() = runTest {
        given { mockSavedStateHandle.get<String>(itemIdKey) }.willReturn(itemId)
        val viewModel = createViewModel()

        viewModel.effects.test {
            testDispatcher.scheduler.runCurrent()

            assertEquals(InitTitle(title), awaitItem())
            assertEquals(InitDescription(description), awaitItem())
            assertEquals(InitIconUrl(iconUrl), awaitItem())
            ensureAllEventsConsumed()
        }
        assertEquals(
            ItemViewState(
                buttonText = R.string.item_edit_button_title,
                isLoading = false,
            ),
            viewModel.state.value,
        )
    }

    @Test
    fun `GIVEN edit item mode but can't get an item WHEN init THEN display error message`() = runTest {
        given { mockSavedStateHandle.get<String>("itemId") }.willReturn(itemId)
        givenBlocking { mockGetTodoItemUseCase.invoke(itemId) }.willReturn(Result.failure(Exception("Error")))
        val viewModel = createViewModel()

        viewModel.effects.test {
            testDispatcher.scheduler.runCurrent()

            assertEquals(DisplayMessage("Error"), awaitItem())
            ensureAllEventsConsumed()
        }
        assertEquals(
            ItemViewState(
                buttonText = R.string.item_edit_button_title,
                isLoading = false,
            ),
            viewModel.state.value,
        )
    }

    @Test
    fun `GIVEN title is empty WHEN button click THEN display title error`() = runTest {
        val viewModel = createViewModel()

        viewModel.effects.test {
            viewModel.onButtonClick("", "", "")
            testDispatcher.scheduler.runCurrent()

            assertEquals(DisplayTitleError, awaitItem())
            ensureAllEventsConsumed()
        }
    }

    @Test
    fun `GIVEN can create an item WHEN button click THEN display confirmation message and close screen`() = runTest {
        given { mockSavedStateHandle.get<String?>(itemIdKey) }.willReturn(null)
        givenBlocking { mockCreateTodoItemUseCase.invoke(title, description, iconUrl) }.willReturn(Result.success(Unit))
        val viewModel = createViewModel()

        viewModel.effects.test {
            viewModel.onButtonClick(title, description, iconUrl)
            testDispatcher.scheduler.runCurrent()

            assertEquals(DisplayMessageRes(R.string.item_create_confirmation_message), awaitItem())
            assertEquals(Close, awaitItem())
            ensureAllEventsConsumed()
        }
        assertEquals(false, viewModel.state.value.isLoading)
    }

    @Test
    fun `GIVEN cannot create an item WHEN button click THEN display an error`() = runTest {
        val errorMessage = "Cannot create item"
        given { mockSavedStateHandle.get<String?>(itemIdKey) }.willReturn(null)
        givenBlocking { mockCreateTodoItemUseCase.invoke(title, description, iconUrl) }.willReturn(Result.failure(Exception(errorMessage)))
        val viewModel = createViewModel()

        viewModel.effects.test {
            viewModel.onButtonClick(title, description, iconUrl)
            testDispatcher.scheduler.runCurrent()

            assertEquals(DisplayMessage(errorMessage), awaitItem())
            ensureAllEventsConsumed()
        }
        assertEquals(false, viewModel.state.value.isLoading)
    }

    @Test
    fun `GIVEN can edit an item WHEN button click THEN display confirmation message and close screen`() = runTest {
        given { mockSavedStateHandle.get<String?>(itemIdKey) }.willReturn(itemId)
        givenBlocking { mockEditTodoItemUseCase.invoke(itemId, title, description, iconUrl) }.willReturn(Result.success(Unit))
        val viewModel = createViewModel()
        testDispatcher.scheduler.runCurrent()

        viewModel.effects.test {
            viewModel.onButtonClick(title, description, iconUrl)
            testDispatcher.scheduler.runCurrent()

            assertEquals(DisplayMessageRes(R.string.item_edit_confirmation_message), awaitItem())
            assertEquals(Close, awaitItem())
            ensureAllEventsConsumed()
        }
        assertEquals(false, viewModel.state.value.isLoading)
    }

    @Test
    fun `GIVEN cannot edit an item WHEN button click THEN display an error`() = runTest {
        val errorMessage = "Cannot edit item"
        given { mockSavedStateHandle.get<String?>(itemIdKey) }.willReturn(itemId)
        givenBlocking { mockEditTodoItemUseCase.invoke(itemId, title, description, iconUrl) }
            .willReturn(Result.failure(Exception(errorMessage)))
        val viewModel = createViewModel()
        testDispatcher.scheduler.runCurrent()

        viewModel.effects.test {
            viewModel.onButtonClick(title, description, iconUrl)
            testDispatcher.scheduler.runCurrent()

            assertEquals(DisplayMessage(errorMessage), awaitItem())
            ensureAllEventsConsumed()
        }
        assertEquals(false, viewModel.state.value.isLoading)
    }

    @Test
    fun `GIVEN icon url is empty WHEN create item THEN propagate null value`() = runTest {
        given { mockSavedStateHandle.get<String?>(itemIdKey) }.willReturn(null)
        givenBlocking { mockCreateTodoItemUseCase.invoke(title, description, iconUrl) }.willReturn(Result.success(Unit))
        val viewModel = createViewModel()

        viewModel.onButtonClick(title, description, "")
        testDispatcher.scheduler.runCurrent()

        verifyBlocking(mockCreateTodoItemUseCase) { invoke(title, description, null) }
    }

    @Test
    fun `WHEN show snackbar THEN emit snackbar message`() = runTest {
        val message = "message"
        val viewModel = createViewModel()

        viewModel.showSnackbar(message)

        verifyBlocking(mockSnackbarFlow) { emit(message) }
    }

    private fun createViewModel() = ItemViewModel(
        savedStateHandle = mockSavedStateHandle,
        getTodoItemUseCase = mockGetTodoItemUseCase,
        createTodoItemUseCase = mockCreateTodoItemUseCase,
        editTodoItemUseCase = mockEditTodoItemUseCase,
        itemConfirmationMessageMapper = ItemConfirmationMessageMapper(),
        snackbarFlow = mockSnackbarFlow,
    )
}
