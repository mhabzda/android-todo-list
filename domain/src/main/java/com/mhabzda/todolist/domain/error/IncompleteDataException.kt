package com.mhabzda.todolist.domain.error

sealed class IncompleteDataException : Exception() {
    data object ItemTitleMissingException : IncompleteDataException()
    data object ItemCreationTimeMissingException : IncompleteDataException()
}