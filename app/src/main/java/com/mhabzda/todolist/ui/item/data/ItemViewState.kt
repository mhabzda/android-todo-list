package com.mhabzda.todolist.ui.item.data

import com.mhabzda.todolist.R

data class ItemViewState(
    var title: String = "",
    var description: String = "",
    var iconUrl: String = "",
    val buttonText: Int = R.string.item_creation_button_title,
    val isLoading: Boolean = false
)
