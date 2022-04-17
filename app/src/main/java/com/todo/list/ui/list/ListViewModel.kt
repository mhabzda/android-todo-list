package com.todo.list.ui.list

import androidx.lifecycle.viewModelScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import com.todo.list.model.repository.TodoRepository
import com.todo.list.ui.base.BaseViewModel
import com.todo.list.ui.list.data.ListViewEvent
import com.todo.list.ui.list.data.ListViewEvent.Error
import com.todo.list.ui.list.data.ListViewState
import com.todo.list.ui.list.navigation.ListRouter
import com.todo.list.utils.onTerminate
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.joda.time.DateTime
import javax.inject.Inject

class ListViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    private val router: ListRouter
) : BaseViewModel<ListViewState, ListViewEvent>(ListViewState()) {

    val pagingEvents = todoRepository.fetchTodoItems(PAGE_SIZE)

    fun onStart() = viewModelScope.launch {
        todoRepository.observeItemsChanges().collectLatest {
            onItemsRefresh()
        }
    }

    fun onLoadStateChange(state: CombinedLoadStates) = viewModelScope.launch {
        updateState { copy(isRefreshing = state.refresh is LoadState.Loading || state.append is LoadState.Loading) }

        val refreshState = state.refresh
        val appendState = state.append
        if (refreshState is LoadState.Error) sendEvent(Error(refreshState.error.message.orEmpty()))
        if (appendState is LoadState.Error) sendEvent(Error(appendState.error.message.orEmpty()))
    }

    fun onItemsRefresh() = viewModelScope.launch {
        sendEvent(ListViewEvent.RefreshItems)
    }

    fun onFloatingButtonClick() {
        router.openItemCreationView()
    }

    fun onItemLongClick(id: DateTime) {
        router.openDeleteItemConfirmationDialog {
            deleteItem(id)
        }
    }

    private fun deleteItem(id: DateTime) = viewModelScope.launch {
        updateState { copy(isRefreshing = true) }
        todoRepository.deleteItem(id)
            .onSuccess { sendEvent(ListViewEvent.DisplayDeletionConfirmation) }
            .onFailure { sendEvent(Error(it.message.orEmpty())) }
            .onTerminate { updateState { copy(isRefreshing = false) } }
    }

    fun onItemClick(id: DateTime) {
        router.openItemEditionView(id)
    }

    companion object {
        private const val PAGE_SIZE = 10
    }
}
