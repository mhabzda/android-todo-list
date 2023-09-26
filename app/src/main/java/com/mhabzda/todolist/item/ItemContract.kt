package com.mhabzda.todolist.item

import androidx.annotation.StringRes
import com.mhabzda.todolist.R

interface ItemContract {
    data class ItemViewState(
        val buttonText: Int = R.string.item_creation_button_title,
        val isLoading: Boolean = false
    )

    sealed class ItemEffect {
        data class InitTitle(val title: String) : ItemEffect()
        data class InitDescription(val description: String) : ItemEffect()
        data class InitIconUrl(val iconUrl: String) : ItemEffect()
        data class DisplayMessageRes(@StringRes val messageRes: Int) : ItemEffect()
        data class DisplayMessage(val message: String) : ItemEffect()
        data object Close : ItemEffect()
    }
}