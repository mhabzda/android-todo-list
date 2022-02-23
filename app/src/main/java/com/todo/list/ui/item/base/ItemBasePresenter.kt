package com.todo.list.ui.item.base

import com.todo.list.utils.EMPTY
import com.todo.list.utils.onTerminate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

abstract class ItemBasePresenter(
    private val view: ItemBaseContract.View
) : ItemBaseContract.Presenter {

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    override fun itemButtonClicked(title: String, description: String, iconUrl: String?) {
        if (title.isEmpty()) {
            view.displayEmptyTitleError()
            return
        }

        coroutineScope.launch {
            view.toggleLoading(true)
            performItemOperation(title, description, iconUrl)
                .onSuccess {
                    view.displayConfirmationMessage()
                    view.close()
                }
                .onFailure { view.displayError(it.message ?: EMPTY) }
                .onTerminate { view.toggleLoading(false) }
        }
    }

    override fun releaseResources() {
        coroutineScope.cancel()
    }

    abstract suspend fun performItemOperation(title: String, description: String, iconUrl: String?): Result<Unit>
}
