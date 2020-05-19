package com.todo.list.di

import com.todo.list.ui.item.creation.ItemCreationActivity
import com.todo.list.ui.item.creation.ItemCreationContract
import com.todo.list.ui.item.creation.ItemCreationPresenter
import dagger.Binds
import dagger.Module

@Module
abstract class ItemCreationViewModule {
  @Binds
  abstract fun provideItemCreationView(itemCreationActivity: ItemCreationActivity): ItemCreationContract.View

  @Binds
  abstract fun provideItemCreationPresenter(presenter: ItemCreationPresenter): ItemCreationContract.Presenter
}