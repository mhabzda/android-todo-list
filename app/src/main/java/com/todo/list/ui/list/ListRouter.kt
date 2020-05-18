package com.todo.list.ui.list

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AlertDialog
import com.todo.list.R
import com.todo.list.di.annotation.ActivityScope
import com.todo.list.ui.creation.ItemCreationActivity
import javax.inject.Inject

@ActivityScope
class ListRouter @Inject constructor(
  private val navigationContext: Context
) : ListContract.Router {
  override fun openItemCreationView() {
    val intent = Intent(navigationContext, ItemCreationActivity::class.java)
    navigationContext.startActivity(intent)
  }

  override fun openDeleteItemConfirmationDialog(deleteItemAction: () -> Unit) {
    AlertDialog.Builder(navigationContext)
      .setTitle(R.string.delete_item_dialog_title)
      .setMessage(R.string.delete_item_dialog_message)
      .setPositiveButton(R.string.delete_item_dialog_delete_button) { _, _ -> deleteItemAction() }
      .setNegativeButton(R.string.delete_item_dialog_cancel_button) { dialog, _ -> dialog.dismiss() }
      .create()
      .show()
  }
}