package com.todo.list.ui.item

import android.os.Bundle
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
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
        binding = ActivityItemBinding.inflate(layoutInflater)
        setContentView(binding.root)
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

    private fun setupListeners() = with(binding) {
        editTextTitle.doAfterTextChanged { viewModel.onTitleChange(it.toString()) }
        editTextDescription.doAfterTextChanged { viewModel.onDescriptionChange(it.toString()) }
        editTextIconUrl.doAfterTextChanged { viewModel.onIconUrlChange(it.toString()) }

        buttonConfirm.loadingButton.setOnClickListener { viewModel.onItemButtonClick() }
    }

    private fun renderState(state: ItemViewState) = with(binding) {
        buttonConfirm.loadingButtonProgressBar.isVisible = state.isLoading
        buttonConfirm.loadingButton.isVisible = !state.isLoading

        editTextTitle.setTextIfDiffer(state.title)
        editTextDescription.setTextIfDiffer(state.description)
        editTextIconUrl.setTextIfDiffer(state.iconUrl)

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

    private fun EditText.setTextIfDiffer(text: String) {
        if (this.text.toString() != text) setText(text)
    }

    companion object {
        const val ITEM_PARCELABLE_EXTRA_KEY = "item_parcelable_extra"
    }
}
