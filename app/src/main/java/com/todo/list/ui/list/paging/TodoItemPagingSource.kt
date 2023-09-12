package com.todo.list.ui.list.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mhabzda.todolist.domain.model.TodoItem
import com.mhabzda.todolist.domain.usecase.GetTodoItemListUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TodoItemPagingSource(
    private val getTodoItemListUseCase: GetTodoItemListUseCase,
) : PagingSource<Int, TodoItem>() {

    private var itemIdFrom: String? = null
    private var pageNumber: Int = 0

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TodoItem> =
        getTodoItemListUseCase.invoke(pageSize = params.loadSize, itemIdFrom = itemIdFrom)
            .fold(
                onSuccess = { itemsList ->
                    resolveNextPage(itemsList, params)
                    LoadResult.Page(
                        data = itemsList,
                        prevKey = null,
                        nextKey = if (isEndOfData(itemsList, params)) null else pageNumber,
                    )
                },
                onFailure = {
                    LoadResult.Error(it)
                }
            )

    private fun resolveNextPage(itemsList: List<TodoItem>, params: LoadParams<Int>) {
        if (isEndOfData(itemsList, params)) {
            itemIdFrom = null
            pageNumber = 0
        } else {
            itemIdFrom = itemsList.last().id
            pageNumber++
        }
    }

    private fun isEndOfData(itemsList: List<TodoItem>, params: LoadParams<Int>) =
        itemsList.size != params.loadSize

    override fun getRefreshKey(state: PagingState<Int, TodoItem>): Int? =
        state.anchorPosition
}
