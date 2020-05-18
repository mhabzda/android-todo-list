package com.todo.list.di

import com.todo.list.ui.creation.ItemCreationActivity
import com.todo.list.ui.creation.ItemCreationContract
import dagger.Binds
import dagger.Module

@Module
abstract class ItemCreationViewModule {
  @Binds
  abstract fun provideItemCreationView(itemCreationActivity: ItemCreationActivity): ItemCreationContract.View
}