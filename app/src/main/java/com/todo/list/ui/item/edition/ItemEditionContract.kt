package com.todo.list.ui.item.edition

import com.todo.list.ui.item.creation.ItemCreationContract

interface ItemEditionContract {
  interface View : ItemCreationContract.View
  interface Presenter : ItemCreationContract.Presenter
}