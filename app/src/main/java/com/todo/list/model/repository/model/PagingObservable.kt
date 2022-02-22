package com.todo.list.model.repository.model

import androidx.paging.PagedList
import com.todo.list.model.entities.TodoItem
import io.reactivex.Observable

data class PagingObservable(
    val pagedList: Observable<PagedList<TodoItem>>,
    val networkState: Observable<NetworkState>
)
