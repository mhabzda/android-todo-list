package com.todo.list.ui.item.mapper

import com.todo.list.model.entities.TodoItem
import com.todo.list.ui.item.data.ItemViewState
import com.todo.list.ui.item.mode.ItemScreenMode
import org.joda.time.DateTime
import javax.inject.Inject

class ItemViewStateMapper @Inject constructor() {

    fun map(
        state: ItemViewState,
        screenMode: ItemScreenMode,
        creationDate: String?
    ) = with(state) {
        when (screenMode) {
            ItemScreenMode.CREATE -> TodoItem.create(title, description, iconUrl)
            ItemScreenMode.EDIT -> TodoItem(title, description, DateTime(creationDate), iconUrl)
        }
    }
}
