package com.mhabzda.todolist.ui.item.mapper

import com.mhabzda.todolist.R
import com.mhabzda.todolist.ui.item.mode.ItemScreenMode
import javax.inject.Inject

class ItemConfirmationMessageMapper @Inject constructor() {

    fun map(screenMode: ItemScreenMode) =
        when (screenMode) {
            ItemScreenMode.CREATE -> R.string.item_creation_confirmation_message
            ItemScreenMode.EDIT -> R.string.item_edition_confirmation_message
        }
}
