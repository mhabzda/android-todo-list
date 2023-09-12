package com.todo.list.ui.list.navigation

import android.app.AlertDialog
import androidx.navigation.NavController
import com.todo.list.R
import com.todo.list.ui.list.ListFragmentDirections
import javax.inject.Inject

class ListRouterImpl @Inject constructor(
    private val navigationController: NavController
) : ListRouter {
    override fun openItemCreationView() {
        navigationController.navigate(ListFragmentDirections.actionToItemFragment())
    }

    override fun openItemEditionView(id: String) {
        navigationController.navigate(ListFragmentDirections.actionToItemFragment(id))
    }

    override fun openDeleteItemConfirmationDialog(deleteItemAction: () -> Unit) {
        AlertDialog.Builder(navigationController.context)
            .setTitle(R.string.delete_item_dialog_title)
            .setMessage(R.string.delete_item_dialog_message)
            .setPositiveButton(R.string.delete_item_dialog_delete_button) { _, _ -> deleteItemAction() }
            .setNegativeButton(R.string.delete_item_dialog_cancel_button) { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }
}
