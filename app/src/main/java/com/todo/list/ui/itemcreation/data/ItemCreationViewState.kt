package com.todo.list.ui.itemcreation.data

import com.todo.list.R

data class ItemCreationViewState(
    val title: String = "",
    val description: String = "",
    val iconUrl: String = "",
    val buttonText: Int = R.string.item_creation_button_title,
    val isLoading: Boolean = false
)
