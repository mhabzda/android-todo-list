package com.todo.list.ui.list

import androidx.lifecycle.viewModelScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import com.todo.list.model.entities.TodoItem
import com.todo.list.model.repository.TodoRepository
import com.todo.list.ui.base.BaseViewModel
import com.todo.list.ui.list.data.ListViewEvent
import com.todo.list.ui.list.data.ListViewEvent.Error
import com.todo.list.ui.list.data.ListViewState
import com.todo.list.ui.list.navigation.ListRouter
import com.todo.list.ui.parcel.TodoItemToParcelableMapper
import com.todo.list.utils.EMPTY
import com.todo.list.utils.onTerminate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class ListViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    private val router: ListRouter,
    private val todoItemToParcelableMapper: TodoItemToParcelableMapper
) : BaseViewModel<ListViewState, ListViewEvent>(ListViewState()) {

    val pagingEvents = todoRepository.fetchTodoItems(PAGE_SIZE)

    fun onCreate(loadStateFlow: Flow<CombinedLoadStates>) {
        viewModelScope.launch {
            loadStateFlow.collectLatest(::handleLoadState)
        }

        observeItemsChanges()
    }

    private suspend fun handleLoadState(state: CombinedLoadStates) {
        updateState { copy(isRefreshing = state.refresh is LoadState.Loading || state.append is LoadState.Loading) }

        val refreshState = state.refresh
        val appendState = state.append
        if (refreshState is LoadState.Error) sendEvent(Error(refreshState.error.message ?: ""))
        if (appendState is LoadState.Error) sendEvent(Error(appendState.error.message ?: ""))
    }

    private fun observeItemsChanges() = viewModelScope.launch {
        todoRepository.observeItemsChanges().collectLatest {
            onItemsRefresh()
        }
    }

    fun onItemsRefresh() = viewModelScope.launch {
        sendEvent(ListViewEvent.RefreshItems)
    }

    fun onFloatingButtonClick() {
        router.openItemCreationView()
    }

    fun onItemLongClick(item: TodoItem) {
        router.openDeleteItemConfirmationDialog {
            deleteItem(item)
        }
    }

    private fun deleteItem(item: TodoItem) = viewModelScope.launch {
        updateState { copy(isRefreshing = true) }
        todoRepository.deleteItem(item)
            .onSuccess { sendEvent(ListViewEvent.DisplayDeletionConfirmation) }
            .onFailure { sendEvent(Error(it.message ?: EMPTY)) }
            .onTerminate { updateState { copy(isRefreshing = false) } }
    }

    fun onItemClick(item: TodoItem) {
        router.openItemEditionView(todoItemToParcelableMapper.map(item))
    }

    companion object {
        private const val PAGE_SIZE = 10
    }
}
