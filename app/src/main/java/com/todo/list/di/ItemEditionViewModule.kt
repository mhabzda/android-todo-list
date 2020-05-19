package com.todo.list.di

import com.todo.list.ui.item.edition.ItemEditionActivity
import com.todo.list.ui.item.edition.ItemEditionContract
import com.todo.list.ui.item.edition.ItemEditionPresenter
import dagger.Binds
import dagger.Module

@Module
abstract class ItemEditionViewModule {
  @Binds
  abstract fun provideItemEditionView(itemEditionActivity: ItemEditionActivity): ItemEditionContract.View

  @Binds
  abstract fun provideItemEditionPresenter(presenter: ItemEditionPresenter): ItemEditionContract.Presenter
}