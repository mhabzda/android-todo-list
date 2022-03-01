package com.todo.list.ui.list.navigation

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AlertDialog
import com.todo.list.R
import com.todo.list.ui.item.ItemActivity
import com.todo.list.ui.item.ItemActivity.Companion.ITEM_ID_EXTRA_KEY
import org.joda.time.DateTime
import javax.inject.Inject

class ListRouterImpl @Inject constructor(
    private val navigationContext: Context
) : ListRouter {
    override fun openItemCreationView() {
        val intent = Intent(navigationContext, ItemActivity::class.java)
        navigationContext.startActivity(intent)
    }

    override fun openItemEditionView(id: DateTime) {
        val intent = Intent(navigationContext, ItemActivity::class.java)
        intent.putExtra(ITEM_ID_EXTRA_KEY, id)
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
