package com.mhabzda.todolist.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import com.mhabzda.todolist.R
import com.mhabzda.todolist.databinding.FragmentListBinding
import com.mhabzda.todolist.ui.list.adapter.ListAdapter
import com.mhabzda.todolist.ui.list.data.ListViewEvent
import com.mhabzda.todolist.utils.observeWhenStarted
import javax.inject.Inject

class ListFragment : Fragment() {

    @Inject
    lateinit var viewModel: ListViewModel

    private lateinit var binding: FragmentListBinding
    private lateinit var listAdapter: ListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        listAdapter = ListAdapter(
            longClickAction = { },
            clickAction = { }
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentListBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupList()

        viewModel.events.observeWhenStarted(viewLifecycleOwner, ::handleEvent)
        viewModel.pagingEvents.observeWhenStarted(viewLifecycleOwner, listAdapter::submitData)
        //listAdapter.loadStateFlow.observeWhenStarted(viewLifecycleOwner, viewModel::onLoadStateChange)

        viewModel.onStart()
    }

    private fun setupList() {
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
