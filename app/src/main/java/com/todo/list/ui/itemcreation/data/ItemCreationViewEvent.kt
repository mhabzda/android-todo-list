package com.todo.list.ui.itemcreation.data

import androidx.annotation.StringRes

sealed class ItemCreationViewEvent {

    object Close : ItemCreationViewEvent()

    data class DisplayMessageRes(@StringRes val messageRes: Int) : ItemCreationViewEvent()

    data class DisplayMessage(val message: String) : ItemCreationViewEvent()
}
