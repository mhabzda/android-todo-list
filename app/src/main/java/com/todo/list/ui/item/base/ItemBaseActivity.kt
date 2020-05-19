package com.todo.list.ui.item.base

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import com.todo.list.R
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_item.edit_text_description as editTextDescription
import kotlinx.android.synthetic.main.activity_item.edit_text_icon_url as editTextIconUrl
import kotlinx.android.synthetic.main.activity_item.edit_text_title as editTextTitle
import kotlinx.android.synthetic.main.loading_button.loading_button as itemActionButton
import kotlinx.android.synthetic.main.loading_button.loading_button_progress_bar as progressBar
import kotlinx.android.synthetic.main.toolbar.my_toolbar as toolbar

abstract class ItemBaseActivity : DaggerAppCompatActivity(), ItemBaseContract.View {

  abstract val presenter: ItemBaseContract.Presenter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_item)
    setSupportActionBar(toolbar)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)

    itemActionButton.setOnClickListener {
      presenter.itemButtonClicked(
        title = editTextTitle.text.toString(),
        description = editTextDescription.text.toString(),
        iconUrl = editTextIconUrl.text.toString()
      )
    }
  }

  override fun toggleLoading(isLoading: Boolean) {
    if (isLoading) {
      progressBar.visibility = View.VISIBLE
      itemActionButton.visibility = View.GONE
    } else {
      progressBar.visibility = View.GONE
      itemActionButton.visibility = View.VISIBLE
    }
  }

  override fun close() {
    finish()
  }

  override fun displayError(errorMessage: String) {
    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
  }

  override fun displayEmptyTitleError() {
    displayToastMessage(R.string.item_empty_title_message)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return if (item.itemId == android.R.id.home) {
      finish()
      true
    } else super.onOptionsItemSelected(item)
  }

  override fun onDestroy() {
    presenter.releaseResources()
    super.onDestroy()
  }

  protected fun displayToastMessage(@StringRes messageId: Int) {
    Toast.makeText(this, messageId, Toast.LENGTH_SHORT).show()
  }
}