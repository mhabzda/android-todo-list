package com.mhabzda.todolist.item.mapper

import com.mhabzda.todolist.R
import com.mhabzda.todolist.item.mode.ItemScreenMode
import javax.inject.Inject

class ItemConfirmationMessageMapper @Inject constructor() {

    fun map(screenMode: ItemScreenMode) =
        when (screenMode) {
            ItemScreenMode.CREATE -> R.string.item_create_confirmation_message
            ItemScreenMode.EDIT -> R.string.item_edit_confirmation_message
        }
}
