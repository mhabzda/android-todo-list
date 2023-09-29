package com.mhabzda.todolist.list

interface ListContract {
    data class ListViewState(
        val showDeleteLoading: Boolean = false,
    )

    sealed class ListEffect {
        data object RefreshItems : ListEffect()
        data object DisplayDeletionConfirmation : ListEffect()
        data class DisplayError(val message: String) : ListEffect()
    }
}
