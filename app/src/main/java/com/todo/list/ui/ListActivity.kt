package com.todo.list.ui

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedList
import com.todo.list.R
import com.todo.list.di.ViewModelFactory
import com.todo.list.model.entities.TodoItem
import com.todo.list.ui.adapter.ListAdapter
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject
import kotlinx.android.synthetic.main.activity_list.my_toolbar as toolbar
import kotlinx.android.synthetic.main.activity_list.todo_list as todoListView

class ListActivity : DaggerAppCompatActivity(), ListContract.View {
  @Inject
  lateinit var viewModelFactory: ViewModelFactory<ListPresenter>

  @Inject
  lateinit var listAdapter: ListAdapter

  lateinit var presenter: ListContract.Presenter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_list)
    setActionBar(toolbar)

    todoListView.adapter = listAdapter

    presenter = ViewModelProvider(this, viewModelFactory).get(ListPresenter::class.java)
    presenter.fetchItems()
  }

  override fun displayTodoList(items: PagedList<TodoItem>) {
    listAdapter.submitList(items)
  }
}
