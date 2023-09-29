package com.mhabzda.todolist.list

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.mhabzda.todolist.base.BaseViewModel
import com.mhabzda.todolist.domain.usecase.DeleteTodoItemUseCase
import com.mhabzda.todolist.domain.usecase.GetTodoItemListUseCase
import com.mhabzda.todolist.list.ListContract.ListEffect
import com.mhabzda.todolist.list.ListContract.ListEffect.DisplayDeletionConfirmation
import com.mhabzda.todolist.list.ListContract.ListEffect.DisplayError
import com.mhabzda.todolist.list.ListContract.ListEffect.RefreshItems
import com.mhabzda.todolist.list.ListContract.ListViewState
import com.mhabzda.todolist.list.paging.TodoItemPagingSource
import com.mhabzda.todolist.util.SnackbarFlow
import com.mhabzda.todolist.util.onTerminate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    private val getTodoItemListUseCase: GetTodoItemListUseCase,
    private val deleteTodoItemUseCase: DeleteTodoItemUseCase,
    snackbarFlow: SnackbarFlow,
) : BaseViewModel<ListViewState, ListEffect>(
    defaultState = ListViewState(),
    emitSnackbarMessage = snackbarFlow::emit,
) {

    val pagingFlow = Pager(PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false)) {
        TodoItemPagingSource(getTodoItemListUseCase)
    }.flow

    fun deleteItem(id: String) = viewModelScope.launch {
        updateState { copy(showDeleteLoading = true) }
        deleteTodoItemUseCase.invoke(id)
            .onSuccess {
                sendEffect(DisplayDeletionConfirmation)
                sendEffect(RefreshItems)
            }
            .onFailure { sendEffect(DisplayError(it.message.orEmpty())) }
            .onTerminate { updateState { copy(showDeleteLoading = false) } }
    }

    companion object {
        private const val PAGE_SIZE = 10
    }
}
