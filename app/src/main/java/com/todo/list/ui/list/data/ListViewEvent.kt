package com.todo.list.ui.list.data

sealed class ListViewEvent {

    object RefreshItems : ListViewEvent()

    object DisplayDeletionConfirmation : ListViewEvent()

    data class Error(val message: String) : ListViewEvent()
}
