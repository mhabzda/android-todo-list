package com.todo.list.ui.creation

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.todo.list.R
import com.todo.list.di.ViewModelFactory
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

class ItemCreationActivity : DaggerAppCompatActivity(), ItemCreationContract.View {
  @Inject
  lateinit var viewModelFactory: ViewModelFactory<ItemCreationPresenter>

  private lateinit var presenter: ItemCreationContract.Presenter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_list)

    presenter = ViewModelProvider(this, viewModelFactory).get(ItemCreationPresenter::class.java)
  }
}