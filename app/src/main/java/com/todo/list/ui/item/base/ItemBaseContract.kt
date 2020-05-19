package com.todo.list.ui.item.base

interface ItemBaseContract {
  interface View {
    fun toggleLoading(isLoading: Boolean)
    fun close()
    fun displayError(errorMessage: String)
    fun displayEmptyTitleError()
    fun displayConfirmationMessage()
  }

  interface Presenter {
    fun itemButtonClicked(title: String, description: String, iconUrl: String?)
    fun releaseResources()
  }
}