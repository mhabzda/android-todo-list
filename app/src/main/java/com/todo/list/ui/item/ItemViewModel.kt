package com.todo.list.ui.item

import androidx.lifecycle.viewModelScope
import com.mhabzda.todolist.domain.usecase.EditTodoItemUseCase
import com.mhabzda.todolist.domain.usecase.GetTodoItemUseCase
import com.mhabzda.todolist.domain.usecase.SaveTodoItemUseCase
import com.todo.list.R
import com.todo.list.ui.base.BaseViewModel
import com.todo.list.ui.item.data.ItemViewEvent
import com.todo.list.ui.item.data.ItemViewEvent.Close
import com.todo.list.ui.item.data.ItemViewEvent.DisplayMessage
import com.todo.list.ui.item.data.ItemViewEvent.DisplayMessageRes
import com.todo.list.ui.item.data.ItemViewState
import com.todo.list.ui.item.mapper.ItemConfirmationMessageMapper
import com.todo.list.ui.item.mode.ItemScreenMode
import com.todo.list.utils.onTerminate
import kotlinx.coroutines.launch
import javax.inject.Inject

class ItemViewModel @Inject constructor(
    private val getTodoItemUseCase: GetTodoItemUseCase,
    private val saveTodoItemUseCase: SaveTodoItemUseCase,
    private val editTodoItemUseCase: EditTodoItemUseCase,
    private val itemConfirmationMessageMapper: ItemConfirmationMessageMapper
) : BaseViewModel<ItemViewState, ItemViewEvent>(ItemViewState()) {

    private lateinit var screenMode: ItemScreenMode
    private lateinit var id: String

    fun onStart(id: String?) {
        screenMode = if (id != null) {
            initializeData(id)
            this.id = id
            updateState { copy(buttonText = R.string.item_edition_button_title) }
            ItemScreenMode.EDIT
        } else {
            updateState { copy(buttonText = R.string.item_creation_button_title) }
            ItemScreenMode.CREATE
        }
    }

    private fun initializeData(id: String) = viewModelScope.launch {
        updateState { copy(isLoading = true) }
        getTodoItemUseCase.invoke(id)
            .onSuccess {
                updateState { copy(title = it.title, description = it.description, iconUrl = it.iconUrl.orEmpty()) }
            }
            .onFailure { sendEvent(DisplayMessage(it.message.orEmpty())) }
            .onTerminate { updateState { copy(isLoading = false) } }
    }

    fun onItemButtonClick() = viewModelScope.launch {
        if (state.value.title.isEmpty()) {
            sendEvent(DisplayMessageRes(R.string.item_empty_title_message))
            return@launch
        }

        updateState { copy(isLoading = true) }

        invokeItemAction()
            .onSuccess {
                sendEvent(DisplayMessageRes(itemConfirmationMessageMapper.map(screenMode)))
                sendEvent(Close)
            }
            .onFailure { sendEvent(DisplayMessage(it.message.orEmpty())) }
            .onTerminate { updateState { copy(isLoading = false) } }
    }

    private suspend fun invokeItemAction(): Result<Unit> = with(state.value) {
        when (screenMode) {
            ItemScreenMode.CREATE -> saveTodoItemUseCase.invoke(
                title = title,
                description = description,
                iconUrl = iconUrl,
            )

            ItemScreenMode.EDIT -> editTodoItemUseCase.invoke(
                id = id,
                title = title,
                description = description,
                iconUrl = iconUrl,
            )
        }
    }
}
