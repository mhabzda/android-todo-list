package com.todo.list.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import com.todo.list.model.entities.TodoItem
import com.todo.list.model.repository.TodoRepository
import com.todo.list.ui.list.data.ListViewEvent
import com.todo.list.ui.list.data.ListViewEvent.Error
import com.todo.list.ui.list.data.ListViewState
import com.todo.list.ui.list.navigation.ListRouter
import com.todo.list.ui.parcel.TodoItemToParcelableMapper
import com.todo.list.utils.EMPTY
import com.todo.list.utils.onTerminate
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class ListViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    private val router: ListRouter,
    private val todoItemToParcelableMapper: TodoItemToParcelableMapper
) : ViewModel() {

    val pagingEvents = todoRepository.fetchTodoItems(PAGE_SIZE)

    private val eventChannel = Channel<ListViewEvent>(Channel.BUFFERED)
    val events = eventChannel.receiveAsFlow()

    private val stateFlow = MutableStateFlow(ListViewState())
    val state = stateFlow.asStateFlow()

    fun onStart(loadStateFlow: Flow<CombinedLoadStates>) {
        viewModelScope.launch {
            loadStateFlow.collectLatest(::handleLoadState)
        }

        observeItemsChanges()
    }

    private suspend fun handleLoadState(state: CombinedLoadStates) {
        stateFlow.value = stateFlow.value.copy(
            isRefreshing = state.refresh is LoadState.Loading || state.append is LoadState.Loading
        )

        val refreshState = state.refresh
        val appendState = state.append
        if (refreshState is LoadState.Error) eventChannel.send(Error(refreshState.error.message ?: ""))
        if (appendState is LoadState.Error) eventChannel.send(Error(appendState.error.message ?: ""))
    }

    private fun observeItemsChanges() = viewModelScope.launch {
        todoRepository.observeItemsChanges().collectLatest {
            refreshItems()
        }
    }

    fun refreshItems() = viewModelScope.launch {
        eventChannel.send(ListViewEvent.RefreshItems)
    }

    fun floatingButtonClicked() {
        router.openItemCreationView()
    }

    fun itemLongClicked(item: TodoItem) {
        router.openDeleteItemConfirmationDialog {
            deleteItem(item)
        }
    }

    private fun deleteItem(item: TodoItem) = viewModelScope.launch {
        stateFlow.value = stateFlow.value.copy(isRefreshing = true)
        todoRepository.deleteItem(item)
            .onSuccess { eventChannel.send(ListViewEvent.DisplayDeletionConfirmation) }
            .onFailure { eventChannel.send(Error(it.message ?: EMPTY)) }
            .onTerminate { stateFlow.value = stateFlow.value.copy(isRefreshing = false) }
    }

    fun itemClicked(item: TodoItem) {
        router.openItemEditionView(todoItemToParcelableMapper.map(item))
    }

    companion object {
        private const val PAGE_SIZE = 10
    }
}
