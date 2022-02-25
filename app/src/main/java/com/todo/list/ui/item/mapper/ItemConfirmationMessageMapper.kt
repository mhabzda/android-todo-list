package com.todo.list.ui.item.mapper

import com.todo.list.R
import com.todo.list.ui.item.mode.ItemScreenMode
import javax.inject.Inject

class ItemConfirmationMessageMapper @Inject constructor() {

    fun map(screenMode: ItemScreenMode) =
        when (screenMode) {
            ItemScreenMode.CREATE -> R.string.item_creation_confirmation_message
            ItemScreenMode.EDIT -> R.string.item_edition_confirmation_message
        }
}
