package com.todo.list.model.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.todo.list.model.entities.TodoItem
import com.todo.list.model.mapper.TodoDocumentFilter
import com.todo.list.model.mapper.TodoDocumentKeys.CREATION_DATE_KEY
import com.todo.list.model.mapper.TodoDocumentMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
            resolveNextPage(collection, params)
            LoadResult.Page(
                data = formatItems(collection),
                prevKey = null,
                nextKey = if (isEndOfData(collection, params)) null else pageNumber
            )
        } catch (exception: Throwable) {
            LoadResult.Error(exception)
        }
    }

    private fun getItems(params: LoadParams<Int>) =
        Tasks.await(todoCollection
            .orderBy(CREATION_DATE_KEY)
            .run {
                if (shouldFirstPageBeLoaded()) this else startAfter(lastLoadedItem)
            }
            .limit(params.loadSize.toLong())
            .get())

    private fun shouldFirstPageBeLoaded() = pageNumber == 0

    private fun resolveNextPage(collection: QuerySnapshot, params: LoadParams<Int>) {
        pageNumber = if (isEndOfData(collection, params)) {
            0
        } else {
            lastLoadedItem = collection.documents[collection.size() - 1]
            pageNumber + 1
        }
    }

    private fun isEndOfData(collection: QuerySnapshot, params: LoadParams<Int>) =
        collection.size() != params.loadSize

    private fun formatItems(collection: QuerySnapshot): List<TodoItem> {
        return collection.documents
            .filter { document -> todoDocumentFilter.filter(document) }
            .map { document -> todoDocumentMapper.map(document) }
    }

    override fun getRefreshKey(state: PagingState<Int, TodoItem>): Int? {
        return state.anchorPosition
    }
}
