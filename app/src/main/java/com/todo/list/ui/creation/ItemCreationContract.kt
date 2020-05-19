package com.todo.list.ui.creation

interface ItemCreationContract {
  interface View {
    fun toggleLoading(isLoading: Boolean)
    fun close()
    fun displayError(errorMessage: String)
    fun displayEmptyTitleError()
    fun displayConfirmationMessage()
  }

  interface Presenter {
    fun saveItemButtonClicked(title: String, description: String, iconUrl: String?)
    fun releaseResources()
  }
}