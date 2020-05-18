package com.todo.list.ui.list

import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedList
import com.todo.list.R
import com.todo.list.di.ViewModelFactory
import com.todo.list.model.entities.TodoItem
import com.todo.list.ui.adapter.ListAdapter
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject
import kotlinx.android.synthetic.main.activity_list.my_toolbar as toolbar
import kotlinx.android.synthetic.main.activity_list.swipe_refresh as swipeRefresh
import kotlinx.android.synthetic.main.activity_list.todo_list as todoListView

class ListActivity : DaggerAppCompatActivity(), ListContract.View {
  @Inject
  lateinit var viewModelFactory: ViewModelFactory<ListPresenter>

  @Inject
  lateinit var listAdapter: ListAdapter

  private lateinit var presenter: ListContract.Presenter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_list)
    setActionBar(toolbar)

    presenter = ViewModelProvider(this, viewModelFactory).get(ListPresenter::class.java)

    todoListView.adapter = listAdapter
    swipeRefresh.setOnRefreshListener { presenter.refreshItems() }
  }

  override fun onResume() {
    super.onResume()
    presenter.observePagedData()
  }

  override fun onPause() {
    presenter.stopPagedDataObservation()
    super.onPause()
  }

  override fun displayTodoList(items: PagedList<TodoItem>) {
    listAdapter.submitList(items)
  }

  override fun setRefreshingState(isRefreshing: Boolean) {
    swipeRefresh.isRefreshing = isRefreshing
  }

  override fun displayError(errorMessage: String) {
    val message = resources.getString(R.string.error_message)
    val finalMessage = "$message - $errorMessage"
    Toast.makeText(this, finalMessage, Toast.LENGTH_SHORT).show()
  }
}
