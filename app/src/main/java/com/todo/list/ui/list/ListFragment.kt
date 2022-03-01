package com.todo.list.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import com.todo.list.R
import com.todo.list.databinding.FragmentListBinding
import com.todo.list.ui.list.adapter.ListAdapter
import com.todo.list.ui.list.data.ListViewEvent
import com.todo.list.utils.observeWhenStarted
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class ListFragment : DaggerFragment() {

    @Inject
    lateinit var viewModel: ListViewModel

    private lateinit var binding: FragmentListBinding
    private lateinit var listAdapter: ListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentListBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapter()

        viewModel.events.observeWhenStarted(viewLifecycleOwner, ::handleEvent)
        viewModel.pagingEvents.onEach(listAdapter::submitData).launchIn(viewLifecycleOwner.lifecycleScope)
        viewModel.onStart(listAdapter.loadStateFlow)
    }

    private fun setupAdapter() {
        listAdapter = ListAdapter(
            longClickAction = { viewModel.onItemLongClick(it) },
            clickAction = { viewModel.onItemClick(it) }
        )
        binding.todoList.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        binding.todoList.adapter = listAdapter
    }

    private fun handleEvent(event: ListViewEvent) {
        when (event) {
            ListViewEvent.RefreshItems -> listAdapter.refresh()
            ListViewEvent.DisplayDeletionConfirmation -> displayItemDeletionConfirmationMessage()
            is ListViewEvent.Error -> Toast.makeText(requireActivity(), event.message, Toast.LENGTH_LONG).show()
        }
    }

    private fun displayItemDeletionConfirmationMessage() {
        Toast.makeText(requireActivity(), R.string.delete_item_confirmation_message, Toast.LENGTH_SHORT).show()
    }
}
