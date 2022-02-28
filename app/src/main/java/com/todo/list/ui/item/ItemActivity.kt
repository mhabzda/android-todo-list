package com.todo.list.ui.item

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import com.todo.list.databinding.ActivityItemBinding
import com.todo.list.ui.item.data.ItemViewEvent
import com.todo.list.utils.observeWhenStarted
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.toolbar.toolbar
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
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        setContentView(binding.root)
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
