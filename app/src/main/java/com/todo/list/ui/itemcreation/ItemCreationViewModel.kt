package com.todo.list.ui.itemcreation

import androidx.lifecycle.viewModelScope
import com.todo.list.R
import com.todo.list.model.entities.TodoItem
import com.todo.list.model.repository.TodoRepository
import com.todo.list.ui.base.BaseViewModel
import com.todo.list.ui.itemcreation.data.ItemCreationViewEvent
import com.todo.list.ui.itemcreation.data.ItemCreationViewEvent.Close
import com.todo.list.ui.itemcreation.data.ItemCreationViewEvent.DisplayMessage
import com.todo.list.ui.itemcreation.data.ItemCreationViewEvent.DisplayMessageRes
import com.todo.list.ui.itemcreation.data.ItemCreationViewState
import com.todo.list.utils.EMPTY
import com.todo.list.utils.onTerminate
import kotlinx.coroutines.launch
import javax.inject.Inject

class ItemCreationViewModel @Inject constructor(
    private val todoRepository: TodoRepository
) : BaseViewModel<ItemCreationViewState, ItemCreationViewEvent>(ItemCreationViewState()) {

    fun onCreate() {
        updateState { copy(buttonText = R.string.item_creation_button_title) }
    }

    fun onItemButtonClick() = viewModelScope.launch {
        if (state.value.title.isEmpty()) {
            sendEvent(DisplayMessageRes(R.string.item_empty_title_message))
            return@launch
        }

        updateState { copy(isLoading = true) }
        todoRepository.saveItem(TodoItem.create(state.value.title, state.value.description, state.value.iconUrl))
            .onSuccess {
                sendEvent(DisplayMessageRes(R.string.item_creation_confirmation_message))
                sendEvent(Close)
            }
            .onFailure { sendEvent(DisplayMessage(it.message ?: EMPTY)) }
            .onTerminate { updateState { copy(isLoading = false) } }
    }

    fun onTitleChange(title: String) {
        updateState { copy(title = title) }
    }

    fun onDescriptionChange(description: String) {
        updateState { copy(description = description) }
    }

    fun onIconUrlChange(iconUrl: String) {
        updateState { copy(iconUrl = iconUrl) }
    }
}
