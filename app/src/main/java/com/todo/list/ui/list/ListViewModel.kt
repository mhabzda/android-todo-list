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
import com.todo.list.ui.schedulers.SchedulerProvider
import com.todo.list.utils.EMPTY
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
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
    private val schedulerProvider: SchedulerProvider,
    private val router: ListRouter,
    private val todoItemToParcelableMapper: TodoItemToParcelableMapper
) : ViewModel() {

    val pagingEvents = todoRepository.fetchTodoItems(PAGE_SIZE)

    private val eventChannel = Channel<ListViewEvent>(Channel.BUFFERED)
    val events = eventChannel.receiveAsFlow()

    private val stateFlow = MutableStateFlow(ListViewState())
    val state = stateFlow.asStateFlow()

    private val compositeDisposable = CompositeDisposable()

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
        if (refreshState is LoadState.Error) eventChannel.send(Error(refreshState.error.message ?: ""))
        val appendState = state.append
        if (appendState is LoadState.Error) eventChannel.send(Error(appendState.error.message ?: ""))
    }

    private fun observeItemsChanges() {
        compositeDisposable.add(todoRepository.observeItemsChanges()
            .subscribeBy {
                refreshItems()
            })
    }

    fun refreshItems() = viewModelScope.launch {
        eventChannel.send(ListViewEvent.RefreshItems)
    }

    fun floatingButtonClicked() {
        router.openItemCreationView()
    }

    fun itemLongClicked(item: TodoItem) {
        router.openDeleteItemConfirmationDialog {
            stateFlow.value = stateFlow.value.copy(isRefreshing = true)
            compositeDisposable.add(todoRepository.deleteItem(item)
                .observeOn(schedulerProvider.ui())
                .doOnTerminate { stateFlow.value = stateFlow.value.copy(isRefreshing = false) }
                .subscribeBy(
                    onComplete = {
                        viewModelScope.launch { eventChannel.send(ListViewEvent.DisplayDeletionConfirmation) }
                    },
                    onError = {
                        viewModelScope.launch { eventChannel.send(Error(it.message ?: EMPTY)) }
                    }
                ))
        }
    }

    fun itemClicked(item: TodoItem) {
        router.openItemEditionView(todoItemToParcelableMapper.map(item))
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }

    companion object {
        private const val PAGE_SIZE = 10
    }
}
