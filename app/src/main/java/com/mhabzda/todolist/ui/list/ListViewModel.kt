package com.mhabzda.todolist.ui.list

import androidx.lifecycle.viewModelScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.mhabzda.todolist.domain.usecase.DeleteTodoItemUseCase
import com.mhabzda.todolist.domain.usecase.GetTodoItemListUseCase
import com.mhabzda.todolist.ui.base.BaseViewModel
import com.mhabzda.todolist.ui.list.data.ListViewEvent
import com.mhabzda.todolist.ui.list.data.ListViewEvent.Error
import com.mhabzda.todolist.ui.list.data.ListViewState
import com.mhabzda.todolist.ui.list.paging.TodoItemPagingSource
import com.mhabzda.todolist.utils.onTerminate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    private val getTodoItemListUseCase: GetTodoItemListUseCase,
    private val deleteTodoItemUseCase: DeleteTodoItemUseCase,
) : BaseViewModel<ListViewState, ListViewEvent>(ListViewState()) {

    val pagingEvents = Pager(PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false)) {
        TodoItemPagingSource(getTodoItemListUseCase)
    }.flow

    fun onStart() {
        // TODO refresh only when needed
        onItemsRefresh()
    }

    fun onLoadStateChange(state: CombinedLoadStates) = viewModelScope.launch {
        updateState { copy(isRefreshing = state.refresh is LoadState.Loading || state.append is LoadState.Loading) }

        val refreshState = state.refresh
        val appendState = state.append
        if (refreshState is LoadState.Error) sendEvent(Error(refreshState.error.message.orEmpty()))
        if (appendState is LoadState.Error) sendEvent(Error(appendState.error.message.orEmpty()))
    }

    fun deleteItem(id: String) = viewModelScope.launch {
        updateState { copy(isRefreshing = true) }
        deleteTodoItemUseCase.invoke(id)
            .onSuccess {
                sendEvent(ListViewEvent.DisplayDeletionConfirmation)
                onItemsRefresh()
            }
            .onFailure { sendEvent(Error(it.message.orEmpty())) }
            .onTerminate { updateState { copy(isRefreshing = false) } }
    }

    private fun onItemsRefresh() = viewModelScope.launch {
        sendEvent(ListViewEvent.RefreshItems)
    }

    companion object {
        private const val PAGE_SIZE = 10
    }
}
