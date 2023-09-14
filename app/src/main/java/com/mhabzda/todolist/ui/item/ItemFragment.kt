package com.mhabzda.todolist.ui.item

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import com.mhabzda.todolist.R
import com.mhabzda.todolist.databinding.FragmentItemBinding
import com.mhabzda.todolist.ui.item.data.ItemViewEvent
import com.mhabzda.todolist.utils.observeWhenStarted
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class ItemFragment : DaggerFragment() {

    @Inject
    lateinit var viewModel: ItemViewModel

    private val arguments: ItemFragmentArgs by navArgs()
    private lateinit var binding: FragmentItemBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentItemBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.itemToolbar.toolbar.setupWithNavController(findNavController())
        binding.itemToolbar.toolbar.setTitle(R.string.app_name)

        viewModel.events.observeWhenStarted(viewLifecycleOwner, ::handleEvent)
        viewModel.onStart(arguments.id)
    }

    private fun handleEvent(event: ItemViewEvent) {
        when (event) {
            ItemViewEvent.Close -> findNavController().navigateUp()
            is ItemViewEvent.DisplayMessageRes -> {
                Toast.makeText(requireActivity(), event.messageRes, Toast.LENGTH_SHORT).show()
            }
            is ItemViewEvent.DisplayMessage -> {
                Toast.makeText(requireActivity(), event.message, Toast.LENGTH_LONG).show()
            }
        }
    }
}
