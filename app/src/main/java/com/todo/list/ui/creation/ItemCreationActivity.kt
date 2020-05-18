package com.todo.list.ui.creation

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.todo.list.R
import com.todo.list.di.ViewModelFactory
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject
import kotlinx.android.synthetic.main.loading_button.item_action_button as itemActionButton
import kotlinx.android.synthetic.main.loading_button.item_icon_progress_bar as progressBar
import kotlinx.android.synthetic.main.toolbar.my_toolbar as toolbar

class ItemCreationActivity : DaggerAppCompatActivity(), ItemCreationContract.View {
  @Inject
  lateinit var viewModelFactory: ViewModelFactory<ItemCreationPresenter>

  private lateinit var presenter: ItemCreationContract.Presenter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_item_creation)
    setActionBar(toolbar)

    presenter = ViewModelProvider(this, viewModelFactory).get(ItemCreationPresenter::class.java)
    itemActionButton.setOnClickListener { presenter.saveItemButtonClicked() }
  }

  override fun toggleLoading(isLoading: Boolean) {
    if (isLoading) {
      progressBar.visibility = View.VISIBLE
      itemActionButton.visibility = View.GONE
    } else {
      progressBar.visibility = View.GONE
      itemActionButton.visibility = View.VISIBLE
    }
  }

  override fun close() {
    finish()
  }
}