package com.mhabzda.todolist.ui.list

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.mhabzda.todolist.domain.usecase.DeleteTodoItemUseCase
import com.mhabzda.todolist.domain.usecase.GetTodoItemListUseCase
import com.mhabzda.todolist.ui.base.BaseViewModel
import com.mhabzda.todolist.ui.list.ListContract.ListEffect
import com.mhabzda.todolist.ui.list.ListContract.ListEffect.DisplayDeletionConfirmation
import com.mhabzda.todolist.ui.list.ListContract.ListEffect.Error
import com.mhabzda.todolist.ui.list.ListContract.ListEffect.RefreshItems
import com.mhabzda.todolist.ui.list.ListContract.ListViewState
import com.mhabzda.todolist.ui.list.paging.TodoItemPagingSource
import com.mhabzda.todolist.utils.onTerminate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    private val getTodoItemListUseCase: GetTodoItemListUseCase,
    private val deleteTodoItemUseCase: DeleteTodoItemUseCase,
) : BaseViewModel<ListViewState, ListEffect>(ListViewState()) {

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
            .onFailure { sendEffect(Error(it.message.orEmpty())) }
            .onTerminate { updateState { copy(showDeleteLoading = false) } }
    }

    companion object {
        private const val PAGE_SIZE = 10
    }
}
