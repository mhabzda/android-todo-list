package com.todo.list.ui.item.edition

import com.todo.list.ui.item.base.ItemBaseContract

interface ItemEditionContract {
    interface View : ItemBaseContract.View {
        fun fillItemData(title: String, description: String, iconUrl: String?)
    }

    interface Presenter : ItemBaseContract.Presenter {
        fun initializeItemData()
    }
}
