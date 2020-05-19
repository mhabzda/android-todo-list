package com.todo.list.ui.item.creation

import android.os.Bundle
import com.todo.list.R
import com.todo.list.ui.item.base.ItemBaseActivity
import com.todo.list.ui.item.base.ItemBaseContract
import javax.inject.Inject
import kotlinx.android.synthetic.main.loading_button.loading_button as itemActionButton

class ItemCreationActivity : ItemBaseActivity(), ItemCreationContract.View {
  @Inject
  lateinit var itemCreationPresenter: ItemCreationContract.Presenter

  override val presenter: ItemBaseContract.Presenter
    get() = itemCreationPresenter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    itemActionButton.setText(R.string.item_creation_button_title)
  }

  override fun displayConfirmationMessage() {
    displayToastMessage(R.string.item_creation_confirmation_message)
  }
}