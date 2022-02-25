package com.todo.list.ui.item

import androidx.lifecycle.viewModelScope
import com.todo.list.R
import com.todo.list.model.entities.TodoItem
import com.todo.list.model.repository.TodoRepository
import com.todo.list.ui.base.BaseViewModel
import com.todo.list.ui.item.data.ItemViewEvent
import com.todo.list.ui.item.data.ItemViewEvent.Close
import com.todo.list.ui.item.data.ItemViewEvent.DisplayMessage
import com.todo.list.ui.item.data.ItemViewEvent.DisplayMessageRes
import com.todo.list.ui.item.data.ItemViewState
import com.todo.list.ui.item.mapper.ItemConfirmationMessageMapper
import com.todo.list.ui.item.mapper.ItemViewStateMapper
import com.todo.list.ui.item.mode.ItemScreenMode
import com.todo.list.ui.parcel.TodoItemParcelable
import com.todo.list.utils.EMPTY
import com.todo.list.utils.isNotNull
import com.todo.list.utils.onTerminate
import kotlinx.coroutines.launch
import javax.inject.Inject

class ItemViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    private val itemViewStateMapper: ItemViewStateMapper,
    private val itemConfirmationMessageMapper: ItemConfirmationMessageMapper
) : BaseViewModel<ItemViewState, ItemViewEvent>(ItemViewState()) {

    private lateinit var screenMode: ItemScreenMode
    private var creationDate: String? = null

    fun onCreate(todoItemParcelable: TodoItemParcelable?) {
        screenMode = if (todoItemParcelable.isNotNull()) {
            initializeData(todoItemParcelable)
            creationDate = todoItemParcelable.creationDate
            ItemScreenMode.EDIT
        } else {
            updateState { copy(buttonText = R.string.item_creation_button_title) }
            ItemScreenMode.CREATE
        }
    }

    private fun initializeData(todoItemParcelable: TodoItemParcelable) = updateState {
        copy(
            title = todoItemParcelable.title,
            description = todoItemParcelable.description,
            iconUrl = todoItemParcelable.iconUrl ?: "",
            buttonText = R.string.item_edition_button_title
        )
    }

    fun onItemButtonClick() = viewModelScope.launch {
        if (state.value.title.isEmpty()) {
            sendEvent(DisplayMessageRes(R.string.item_empty_title_message))
            return@launch
        }

        updateState { copy(isLoading = true) }
        resolveItemAction().invoke(itemViewStateMapper.map(state.value, screenMode, creationDate))
            .onSuccess {
                sendEvent(DisplayMessageRes(itemConfirmationMessageMapper.map(screenMode)))
                sendEvent(Close)
            }
            .onFailure { sendEvent(DisplayMessage(it.message ?: EMPTY)) }
            .onTerminate { updateState { copy(isLoading = false) } }
    }

    private fun resolveItemAction(): suspend (TodoItem) -> Result<Unit> =
        when (screenMode) {
            ItemScreenMode.CREATE -> todoRepository::saveItem
            ItemScreenMode.EDIT -> todoRepository::editItem
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
