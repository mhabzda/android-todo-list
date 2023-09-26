package com.mhabzda.todolist.ui.item

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.mhabzda.todolist.Destination.ItemScreen.ITEM_ID_ARG_NAME
import com.mhabzda.todolist.R
import com.mhabzda.todolist.domain.usecase.CreateTodoItemUseCase
import com.mhabzda.todolist.domain.usecase.EditTodoItemUseCase
import com.mhabzda.todolist.domain.usecase.GetTodoItemUseCase
import com.mhabzda.todolist.ui.base.BaseViewModel
import com.mhabzda.todolist.ui.item.ItemContract.ItemEffect
import com.mhabzda.todolist.ui.item.ItemContract.ItemEffect.Close
import com.mhabzda.todolist.ui.item.ItemContract.ItemEffect.DisplayMessage
import com.mhabzda.todolist.ui.item.ItemContract.ItemEffect.DisplayMessageRes
import com.mhabzda.todolist.ui.item.ItemContract.ItemEffect.InitDescription
import com.mhabzda.todolist.ui.item.ItemContract.ItemEffect.InitIconUrl
import com.mhabzda.todolist.ui.item.ItemContract.ItemEffect.InitTitle
import com.mhabzda.todolist.ui.item.ItemContract.ItemViewState
import com.mhabzda.todolist.ui.item.mapper.ItemConfirmationMessageMapper
import com.mhabzda.todolist.ui.item.mode.ItemScreenMode
import com.mhabzda.todolist.utils.onTerminate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ItemViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getTodoItemUseCase: GetTodoItemUseCase,
    private val createTodoItemUseCase: CreateTodoItemUseCase,
    private val editTodoItemUseCase: EditTodoItemUseCase,
    private val itemConfirmationMessageMapper: ItemConfirmationMessageMapper
) : BaseViewModel<ItemViewState, ItemEffect>(ItemViewState()) {

    private val itemId: String? = savedStateHandle[ITEM_ID_ARG_NAME]
    private val screenMode: ItemScreenMode

    init {
        screenMode = if (itemId != null) {
            initializeData(itemId)
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
                sendEffect(InitTitle(it.title))
                sendEffect(InitDescription(it.description))
                sendEffect(InitIconUrl(it.iconUrl.orEmpty()))
            }
            .onFailure { sendEffect(DisplayMessage(it.message.orEmpty())) }
            .onTerminate { updateState { copy(isLoading = false) } }
    }

    fun onButtonClick(title: String, description: String, iconUrl: String) = viewModelScope.launch {
        if (title.isEmpty()) {
            sendEffect(DisplayMessageRes(R.string.item_empty_title_message))
            return@launch
        }

        updateState { copy(isLoading = true) }

        invokeItemAction(title = title, description = description, iconUrl = iconUrl.ifEmpty { null })
            .onSuccess {
                sendEffect(DisplayMessageRes(itemConfirmationMessageMapper.map(screenMode)))
                sendEffect(Close)
            }
            .onFailure { sendEffect(DisplayMessage(it.message.orEmpty())) }
            .onTerminate { updateState { copy(isLoading = false) } }
    }

    private suspend fun invokeItemAction(title: String, description: String, iconUrl: String?): Result<Unit> =
        when (screenMode) {
            ItemScreenMode.CREATE -> createTodoItemUseCase.invoke(
                title = title,
                description = description,
                iconUrl = iconUrl,
            )
            ItemScreenMode.EDIT -> editTodoItemUseCase.invoke(
                id = itemId.orEmpty(),
                title = title,
                description = description,
                iconUrl = iconUrl,
            )
        }
}
