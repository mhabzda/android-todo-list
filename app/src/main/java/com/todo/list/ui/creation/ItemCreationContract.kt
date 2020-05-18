package com.todo.list.ui.creation

interface ItemCreationContract {
  interface View {
    fun toggleLoading(isLoading: Boolean)
    fun close()
  }

  interface Presenter {
    fun saveItemButtonClicked()
  }
}