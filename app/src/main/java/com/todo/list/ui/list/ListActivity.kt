package com.todo.list.ui.list

import android.os.Bundle
import android.widget.Toast
import androidx.paging.PagedList
import com.todo.list.R
import com.todo.list.model.entities.TodoItem
import com.todo.list.ui.list.adapter.ListAdapter
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject
import kotlinx.android.synthetic.main.activity_list.floating_action_button as floatingActionButton
import kotlinx.android.synthetic.main.activity_list.swipe_refresh as swipeRefresh
import kotlinx.android.synthetic.main.activity_list.todo_list as todoListView
import kotlinx.android.synthetic.main.toolbar.my_toolbar as toolbar

class ListActivity : DaggerAppCompatActivity(), ListContract.View {
  @Inject
  lateinit var presenter: ListContract.Presenter

  private lateinit var listAdapter: ListAdapter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_list)
    setSupportActionBar(toolbar)

    listAdapter = ListAdapter { presenter.itemLongClicked(it) }

    todoListView.adapter = listAdapter
    swipeRefresh.setOnRefreshListener { presenter.refreshItems() }
    floatingActionButton.setOnClickListener { presenter.floatingButtonClicked() }

    presenter.observePagedData()
  }

  override fun onDestroy() {
    presenter.clearResources()
    super.onDestroy()
  }

  override fun displayTodoList(items: PagedList<TodoItem>) {
    listAdapter.submitList(items)
  }

  override fun setRefreshingState(isRefreshing: Boolean) {
    swipeRefresh.isRefreshing = isRefreshing
  }

  override fun displayError(errorMessage: String) {
    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
  }
}
