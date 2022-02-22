package com.todo.list.ui.list

import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import com.todo.list.model.entities.TodoItem
import com.todo.list.model.repository.TodoRepository
import com.todo.list.ui.parcel.TodoItemToParcelableMapper
import com.todo.list.ui.schedulers.SchedulerProvider
import com.todo.list.utils.EMPTY
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class ListPresenter @Inject constructor(
    private val todoRepository: TodoRepository,
    private val schedulerProvider: SchedulerProvider,
    private val view: ListContract.View,
    private val router: ListContract.Router,
    private val todoItemToParcelableMapper: TodoItemToParcelableMapper
) : ListContract.Presenter {
    private val compositeDisposable = CompositeDisposable()

    override fun onStart(loadStateFlow: Flow<CombinedLoadStates>) {
        CoroutineScope(Dispatchers.Main).launch {
            loadStateFlow.collectLatest { state ->
                view.setRefreshingState(state.refresh is LoadState.Loading || state.append is LoadState.Loading)

                val refreshState = state.refresh
                if (refreshState is LoadState.Error) view.displayError(refreshState.error.message ?: "")
                val appendState = state.append
                if (appendState is LoadState.Error) view.displayError(appendState.error.message ?: "")
            }
        }

        observePagingData()
    }

    private fun observePagingData() {
        CoroutineScope(Dispatchers.Main).launch {
            todoRepository.fetchTodoItems(PAGE_SIZE)
                .collectLatest {
                    view.displayTodoList(it)
                }
        }

        observeItemsChanges()
    }

    private fun observeItemsChanges() {
        compositeDisposable.add(todoRepository.observeItemsChanges()
            .subscribeBy {
                refreshItems()
            })
    }

    override fun refreshItems() {
        view.refreshListItems()
    }

    override fun floatingButtonClicked() {
        router.openItemCreationView()
    }

    override fun itemLongClicked(item: TodoItem) {
        router.openDeleteItemConfirmationDialog {
            view.setRefreshingState(true)
            compositeDisposable.add(todoRepository.deleteItem(item)
                .observeOn(schedulerProvider.ui())
                .doOnTerminate { view.setRefreshingState(false) }
                .subscribeBy(
                    onComplete = {
                        view.displayItemDeletionConfirmationMessage()
                    },
                    onError = {
                        view.displayError(it.message ?: EMPTY)
                    }
                ))
        }
    }

    override fun itemClicked(item: TodoItem) {
        router.openItemEditionView(todoItemToParcelableMapper.map(item))
    }

    override fun clearResources() {
        compositeDisposable.clear()
    }

    companion object {
        private const val PAGE_SIZE = 10
    }
}
