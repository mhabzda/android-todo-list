package com.todo.list.model.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.todo.list.model.entities.TodoItem
import com.todo.list.model.mapper.TodoDocumentFilter
import com.todo.list.model.mapper.TodoDocumentMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.max

class TodoItemPagingSource(
    private val todoCollection: CollectionReference,
    private val todoDocumentMapper: TodoDocumentMapper,
    private val todoDocumentFilter: TodoDocumentFilter,
) : PagingSource<Int, TodoItem>() {

    private lateinit var lastLoadedItem: DocumentSnapshot
    private var pageNumber: Int = 0

    override suspend fun load(
        params: LoadParams<Int>
    ): LoadResult<Int, TodoItem> {
        return try {
            val collection = withContext(Dispatchers.IO) { getItems(params) }
            lastLoadedItem = collection.documents[max(collection.size() - 1, 0)]
            pageNumber++
            LoadResult.Page(
                data = formatItems(collection),
                prevKey = null,
                nextKey = if (collection.size() == params.loadSize) pageNumber else null
            )
        } catch (exception: Throwable) {
            LoadResult.Error(exception)
        }
    }

    private fun getItems(params: LoadParams<Int>) =
        Tasks.await(todoCollection
            .run {
                if (pageNumber > 0) startAfter(lastLoadedItem) else this
            }
            .limit(params.loadSize.toLong())
            .get())

    private fun formatItems(collection: QuerySnapshot): List<TodoItem> {
        return collection.documents
            .filter { document -> todoDocumentFilter.filter(document) }
            .map { document -> todoDocumentMapper.map(document) }
    }

    override fun getRefreshKey(state: PagingState<Int, TodoItem>): Int? {
        return state.anchorPosition
    }
}
