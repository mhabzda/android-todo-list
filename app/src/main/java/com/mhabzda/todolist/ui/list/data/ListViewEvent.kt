package com.mhabzda.todolist.ui.list.data

sealed class ListViewEvent {

    data object RefreshItems : ListViewEvent()

    data object DisplayDeletionConfirmation : ListViewEvent()

    data class Error(val message: String) : ListViewEvent()
}
