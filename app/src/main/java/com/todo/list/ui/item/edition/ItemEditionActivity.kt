package com.todo.list.ui.item.edition

import android.os.Bundle
import com.todo.list.R
import com.todo.list.ui.item.base.ItemBaseActivity
import com.todo.list.ui.item.base.ItemBaseContract
import javax.inject.Inject
import kotlinx.android.synthetic.main.loading_button.item_action_button as itemActionButton

class ItemEditionActivity : ItemBaseActivity(), ItemEditionContract.View {
  @Inject
  lateinit var itemEditionPresenter: ItemEditionContract.Presenter

  override val presenter: ItemBaseContract.Presenter
    get() = itemEditionPresenter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    itemActionButton.setText(R.string.item_edition_button_title)
  }

  override fun displayConfirmationMessage() {
    displayToastMessage(R.string.item_edition_confirmation_message)
  }
}