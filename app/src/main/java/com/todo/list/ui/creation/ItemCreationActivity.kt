package com.todo.list.ui.creation

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModelProvider
import com.todo.list.R
import com.todo.list.di.ViewModelFactory
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject
import kotlinx.android.synthetic.main.activity_item_creation.edit_text_description as editTextDescription
import kotlinx.android.synthetic.main.activity_item_creation.edit_text_icon_url as editTextIconUrl
import kotlinx.android.synthetic.main.activity_item_creation.edit_text_title as editTextTitle
import kotlinx.android.synthetic.main.loading_button.item_action_button as itemActionButton
import kotlinx.android.synthetic.main.loading_button.item_icon_progress_bar as progressBar
import kotlinx.android.synthetic.main.toolbar.my_toolbar as toolbar

class ItemCreationActivity : DaggerAppCompatActivity(), ItemCreationContract.View {
  @Inject
  lateinit var viewModelFactory: ViewModelFactory<ItemCreationPresenter>

  private lateinit var presenter: ItemCreationContract.Presenter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_item_creation)
    setSupportActionBar(toolbar)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)

    presenter = ViewModelProvider(this, viewModelFactory).get(ItemCreationPresenter::class.java)
    itemActionButton.setOnClickListener {
      presenter.saveItemButtonClicked(
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
    displayToastMessage(R.string.item_creation_empty_title_message)
  }

  override fun displayConfirmationMessage() {
    displayToastMessage(R.string.item_creation_confirmation_message)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return if (item.itemId == android.R.id.home) {
      finish()
      true
    } else super.onOptionsItemSelected(item)
  }

  private fun displayToastMessage(@StringRes messageId: Int) {
    Toast.makeText(this, messageId, Toast.LENGTH_SHORT).show()
  }
}