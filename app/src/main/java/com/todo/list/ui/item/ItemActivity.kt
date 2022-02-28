package com.todo.list.ui.item

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.todo.list.databinding.ActivityItemBinding
import com.todo.list.ui.item.data.ItemViewEvent
import com.todo.list.ui.item.data.ItemViewState
import com.todo.list.utils.observeWhenStarted
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.toolbar.toolbar
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class ItemActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModel: ItemViewModel

    private lateinit var binding: ActivityItemBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupBinding()
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupListeners()
        viewModel.state.onEach(::renderState).launchIn(lifecycleScope)
        viewModel.events.observeWhenStarted(this, ::handleEvent)

        viewModel.onCreate(intent.getParcelableExtra(ITEM_PARCELABLE_EXTRA_KEY))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            finish()
            true
        } else super.onOptionsItemSelected(item)
    }

    private fun setupBinding() {
        binding = ActivityItemBinding.inflate(layoutInflater)
        binding.viewModel = viewModel
        setContentView(binding.root)
    }

    private fun setupListeners() = with(binding) {
        buttonConfirm.loadingButton.setOnClickListener { this@ItemActivity.viewModel.onItemButtonClick() }
    }

    private fun renderState(state: ItemViewState) = with(binding) {
        buttonConfirm.loadingButtonProgressBar.isVisible = state.isLoading
        buttonConfirm.loadingButton.isVisible = !state.isLoading

        itemTitleEditText.setText(state.title)
        itemDescriptionEditText.setText(state.description)
        itemIconUrlEditText.setText(state.iconUrl)

        buttonConfirm.loadingButton.setText(state.buttonText)
    }

    private fun handleEvent(event: ItemViewEvent) {
        when (event) {
            ItemViewEvent.Close -> finish()
            is ItemViewEvent.DisplayMessageRes -> {
                Toast.makeText(this, event.messageRes, Toast.LENGTH_SHORT).show()
            }
            is ItemViewEvent.DisplayMessage -> {
                Toast.makeText(this, event.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    companion object {
        const val ITEM_PARCELABLE_EXTRA_KEY = "item_parcelable_extra"
    }
}
