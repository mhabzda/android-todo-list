package com.todo.list.ui.list

import androidx.paging.CombinedLoadStates
import androidx.paging.PagingData
import com.todo.list.model.entities.TodoItem
import com.todo.list.ui.parcel.TodoItemParcelable
import kotlinx.coroutines.flow.Flow

interface ListContract {
    interface View {
        suspend fun displayTodoList(items: PagingData<TodoItem>)
        fun setRefreshingState(isRefreshing: Boolean)
        fun displayError(errorMessage: String)
        fun displayItemDeletionConfirmationMessage()
        fun refreshListItems()
    }

    interface Presenter {
        fun onStart(loadStateFlow: Flow<CombinedLoadStates>)
        fun refreshItems()
        fun floatingButtonClicked()
        fun itemLongClicked(item: TodoItem)
        fun itemClicked(item: TodoItem)
        fun clearResources()
    }

    interface Router {
        fun openItemCreationView()
        fun openItemEditionView(item: TodoItemParcelable)
        fun openDeleteItemConfirmationDialog(deleteItemAction: () -> Unit)
    }
}
