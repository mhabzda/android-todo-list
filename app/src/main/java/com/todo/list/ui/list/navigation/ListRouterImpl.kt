package com.todo.list.ui.list.navigation

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AlertDialog
import com.todo.list.R
import com.todo.list.ui.item.creation.ItemCreationActivity
import com.todo.list.ui.item.edition.ItemEditionActivity
import com.todo.list.ui.item.edition.ItemEditionActivity.Companion.ITEM_PARCELABLE_EXTRA_KEY
import com.todo.list.ui.parcel.TodoItemParcelable
import javax.inject.Inject

class ListRouterImpl @Inject constructor(
    private val navigationContext: Context
) : ListRouter {
    override fun openItemCreationView() {
        val intent = Intent(navigationContext, ItemCreationActivity::class.java)
        navigationContext.startActivity(intent)
    }

    override fun openItemEditionView(item: TodoItemParcelable) {
        val intent = Intent(navigationContext, ItemEditionActivity::class.java)
        intent.putExtra(ITEM_PARCELABLE_EXTRA_KEY, item)
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
