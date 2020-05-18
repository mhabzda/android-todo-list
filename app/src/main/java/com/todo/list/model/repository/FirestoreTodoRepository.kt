package com.todo.list.model.repository

import androidx.paging.PagedList
import androidx.paging.RxPagedListBuilder
import com.todo.list.model.repository.model.PagingObservable
import com.todo.list.model.repository.source.TodoItemsDataSourceFactory
import javax.inject.Inject

class FirestoreTodoRepository @Inject constructor(
  private val todoItemsDataSourceFactory: TodoItemsDataSourceFactory
) : TodoRepository {
  override fun fetchTodoItems(): PagingObservable {
    val pagedListObservable = RxPagedListBuilder(
      todoItemsDataSourceFactory,
      createPagingConfig()
    ).buildObservable()

    val networkStateObservable = todoItemsDataSourceFactory.networkOperationSubject.hide()

    return PagingObservable(pagedListObservable, networkStateObservable)
  }

  override fun refreshTodoItems() {
    todoItemsDataSourceFactory.dataSource.invalidate()
  }

  private fun createPagingConfig(): PagedList.Config {
    return PagedList.Config.Builder()
      .setEnablePlaceholders(false)
      .setPageSize(PAGE_SIZE)
      .build()
  }

  companion object {
    private const val PAGE_SIZE = 5
  }
}