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
        creationDate: DateTime?
    ) = with(state) {
        when (screenMode) {
            ItemScreenMode.CREATE -> TodoItem(title, description, DateTime.now(), iconUrl)
            ItemScreenMode.EDIT -> TodoItem(title, description, creationDate!!, iconUrl)
        }
    }
}
