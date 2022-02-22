package com.todo.list.ui.list

import android.os.Bundle
import android.widget.Toast
import androidx.paging.PagingData
import androidx.recyclerview.widget.DividerItemDecoration
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

        listAdapter = ListAdapter(
            longClickAction = { presenter.itemLongClicked(it) },
            clickAction = { presenter.itemClicked(it) }
        )

        todoListView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        todoListView.adapter = listAdapter
        swipeRefresh.setOnRefreshListener { presenter.refreshItems() }
        floatingActionButton.setOnClickListener { presenter.floatingButtonClicked() }

        presenter.onStart(listAdapter.loadStateFlow)
    }

    override fun onDestroy() {
        presenter.clearResources()
        super.onDestroy()
    }

    override suspend fun displayTodoList(items: PagingData<TodoItem>) {
        listAdapter.submitData(items)
    }

    override fun setRefreshingState(isRefreshing: Boolean) {
        swipeRefresh.isRefreshing = isRefreshing
    }

    override fun displayError(errorMessage: String) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
    }

    override fun displayItemDeletionConfirmationMessage() {
        Toast.makeText(this, R.string.delete_item_confirmation_message, Toast.LENGTH_SHORT).show()
    }

    override fun refreshListItems() {
        listAdapter.refresh()
    }
}
