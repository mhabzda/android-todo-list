package com.todo.list.ui.item.data

import androidx.annotation.StringRes

sealed class ItemViewEvent {

    data object Close : ItemViewEvent()

    data class DisplayMessageRes(@StringRes val messageRes: Int) : ItemViewEvent()

    data class DisplayMessage(val message: String) : ItemViewEvent()
}
