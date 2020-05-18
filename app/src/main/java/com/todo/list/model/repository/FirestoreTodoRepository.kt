package com.todo.list.model.repository

import androidx.paging.DataSource
import androidx.paging.PagedList
import androidx.paging.RxPagedListBuilder
import com.todo.list.model.entities.TodoItem
import io.reactivex.Observable
import javax.inject.Inject

class FirestoreTodoRepository @Inject constructor(
  private val todoItemsDataSource: TodoItemsDataSource
) : TodoRepository {

  override val networkOperationState: Observable<NetworkOperationState>
    get() = todoItemsDataSource.networkOperationSubject.hide()

  override fun fetchTodoItems(): Observable<PagedList<TodoItem>> {
    val pagingConfig = PagedList.Config.Builder()
      .setEnablePlaceholders(false)
      .setPageSize(PAGE_SIZE)
      .build()

    return RxPagedListBuilder(
      createDataSourceFactory(),
      pagingConfig
    ).buildObservable()
  }

  override fun refreshTodoItems() {
    todoItemsDataSource.invalidate()
  }

  private fun createDataSourceFactory(): DataSource.Factory<Int, TodoItem> {
    return object : DataSource.Factory<Int, TodoItem>() {
      override fun create(): DataSource<Int, TodoItem> {
        return todoItemsDataSource
      }
    }
  }

  companion object {
    private const val PAGE_SIZE = 5
  }
}