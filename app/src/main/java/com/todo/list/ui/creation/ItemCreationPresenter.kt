package com.todo.list.ui.creation

import androidx.lifecycle.ViewModel
import com.todo.list.ui.schedulers.SchedulerProvider
import javax.inject.Inject

class ItemCreationPresenter @Inject constructor(
  private val view: ItemCreationContract.View,
  private val schedulerProvider: SchedulerProvider
) : ViewModel(), ItemCreationContract.Presenter {
  override fun saveItemButtonClicked() {
    view.toggleLoading(true)
  }
}