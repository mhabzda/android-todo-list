package com.todo.list.ui.item.edition

import com.todo.list.ui.item.creation.ItemCreationContract

interface ItemEditionContract {
  interface View : ItemCreationContract.View {
    fun fillItemData(title: String, description: String, iconUrl: String?)
  }

  interface Presenter : ItemCreationContract.Presenter {
    fun initializeItemData()
  }
}