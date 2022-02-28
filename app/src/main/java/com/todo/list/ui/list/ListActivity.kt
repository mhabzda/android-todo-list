package com.todo.list.ui.list

import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import com.todo.list.R
import com.todo.list.databinding.ActivityListBinding
import com.todo.list.ui.list.adapter.ListAdapter
import com.todo.list.ui.list.data.ListViewEvent
import com.todo.list.ui.list.data.ListViewState
import com.todo.list.utils.observeWhenStarted
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class ListActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModel: ListViewModel

    private lateinit var binding: ActivityListBinding
    private lateinit var listAdapter: ListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupBinding()
        setSupportActionBar(binding.listToolbar.toolbar)
        setupAdapter()

        viewModel.state.onEach(::renderState).launchIn(lifecycleScope)
        viewModel.events.observeWhenStarted(this, ::handleEvent)
        viewModel.pagingEvents.onEach(listAdapter::submitData).launchIn(lifecycleScope)

        viewModel.onCreate(listAdapter.loadStateFlow)
    }

    private fun setupBinding() {
        binding = ActivityListBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        setContentView(binding.root)
    }

    private fun setupAdapter() {
        listAdapter = ListAdapter(
            longClickAction = { viewModel.onItemLongClick(it) },
            clickAction = { viewModel.onItemClick(it) }
        )
        binding.todoList.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        binding.todoList.adapter = listAdapter
    }

    private fun renderState(state: ListViewState) {
        binding.swipeRefresh.isRefreshing = state.isRefreshing
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
