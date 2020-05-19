package com.todo.list.ui.item.creation

import com.todo.list.ui.item.base.ItemBaseContract

interface ItemCreationContract {
  interface View : ItemBaseContract.View
  interface Presenter : ItemBaseContract.Presenter
}