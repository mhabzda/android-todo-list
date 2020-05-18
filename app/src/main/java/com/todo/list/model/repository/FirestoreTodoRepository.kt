package com.todo.list.model.repository

import androidx.paging.PagedList
import androidx.paging.RxPagedListBuilder
import com.todo.list.model.entities.TodoItem
import io.reactivex.Observable
import javax.inject.Inject

class FirestoreTodoRepository @Inject constructor(
  private val todoItemsDataSourceFactory: TodoItemsDataSourceFactory
) : TodoRepository {
  override fun fetchTodoItems(): Observable<PagedList<TodoItem>> {
    val pagingConfig = PagedList.Config.Builder()
      .setEnablePlaceholders(false)
      .setPageSize(PAGE_SIZE)
      .build()

    return RxPagedListBuilder(
      todoItemsDataSourceFactory,
      pagingConfig
    ).buildObservable()
  }

  override fun refreshTodoItems() {
    todoItemsDataSourceFactory.invalidateSource()
  }

  companion object {
    private const val PAGE_SIZE = 5
  }
}