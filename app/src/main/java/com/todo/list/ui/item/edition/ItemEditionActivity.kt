package com.todo.list.ui.item.edition

import android.os.Bundle
import com.todo.list.R
import com.todo.list.ui.item.base.ItemBaseActivity
import com.todo.list.ui.item.base.ItemBaseContract
import com.todo.list.ui.parcel.TodoItemParcelable
import com.todo.list.utils.getParcelableStrictly
import javax.inject.Inject
import kotlinx.android.synthetic.main.activity_item.edit_text_description as editTextDescription
import kotlinx.android.synthetic.main.activity_item.edit_text_icon_url as editTextIconUrl
import kotlinx.android.synthetic.main.activity_item.edit_text_title as editTextTitle
import kotlinx.android.synthetic.main.loading_button.item_action_button as itemActionButton

class ItemEditionActivity : ItemBaseActivity(), ItemEditionContract.View {
  @Inject
  lateinit var itemEditionPresenter: ItemEditionContract.Presenter

  override val presenter: ItemBaseContract.Presenter
    get() = itemEditionPresenter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    itemActionButton.setText(R.string.item_edition_button_title)

    itemEditionPresenter.initializeItemData()
  }

  override fun fillItemData(title: String, description: String, iconUrl: String?) {
    editTextTitle.setText(title)
    editTextDescription.setText(description)
    editTextIconUrl.setText(iconUrl)
  }

  override fun displayConfirmationMessage() {
    displayToastMessage(R.string.item_edition_confirmation_message)
  }

  fun provideTodoItemParcelable(): TodoItemParcelable {
    return intent.getParcelableStrictly(ITEM_PARCELABLE_EXTRA_KEY)
  }

  companion object {
    const val ITEM_PARCELABLE_EXTRA_KEY = "item_parcelable_extra"
  }
}