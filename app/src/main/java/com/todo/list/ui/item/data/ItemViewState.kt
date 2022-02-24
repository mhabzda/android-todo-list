package com.todo.list.ui.item.data

import com.todo.list.R

data class ItemViewState(
    val title: String = "",
    val description: String = "",
    val iconUrl: String = "",
    val buttonText: Int = R.string.item_creation_button_title,
    val isLoading: Boolean = false
)
