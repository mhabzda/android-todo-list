package com.todo.list.ui.list

import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import com.todo.list.R
import com.todo.list.ui.list.adapter.ListAdapter
import com.todo.list.ui.list.data.ListViewEvent
import com.todo.list.ui.list.data.ListViewState
import com.todo.list.utils.observeWhenStarted
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import kotlinx.android.synthetic.main.activity_list.floating_action_button as floatingActionButton
import kotlinx.android.synthetic.main.activity_list.swipe_refresh as swipeRefresh
import kotlinx.android.synthetic.main.activity_list.todo_list as todoListView
import kotlinx.android.synthetic.main.toolbar.my_toolbar as toolbar

class ListActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModel: ListViewModel

    private lateinit var listAdapter: ListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        setSupportActionBar(toolbar)

        listAdapter = ListAdapter(
            longClickAction = { viewModel.itemLongClicked(it) },
            clickAction = { viewModel.itemClicked(it) }
        )
        todoListView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        todoListView.adapter = listAdapter

        swipeRefresh.setOnRefreshListener { viewModel.refreshItems() }
        floatingActionButton.setOnClickListener { viewModel.floatingButtonClicked() }

        viewModel.state.onEach(::renderState).launchIn(lifecycleScope)
        viewModel.events.observeWhenStarted(this, ::handleEvent)
        viewModel.pagingEvents.onEach(listAdapter::submitData).launchIn(lifecycleScope)

        viewModel.onStart(listAdapter.loadStateFlow)
    }

    private fun renderState(state: ListViewState) {
        swipeRefresh.isRefreshing = state.isRefreshing
    }

    private fun handleEvent(event: ListViewEvent) {
        when (event) {
            ListViewEvent.RefreshItems -> listAdapter.refresh()
            ListViewEvent.DisplayDeletionConfirmation -> displayItemDeletionConfirmationMessage()
            is ListViewEvent.Error -> Toast.makeText(this, event.message, Toast.LENGTH_LONG).show()
        }
    }

    private fun displayItemDeletionConfirmationMessage() {
        Toast.makeText(this, R.string.delete_item_confirmation_message, Toast.LENGTH_SHORT).show()
    }
}
