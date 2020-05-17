package com.todo.list.di

import com.todo.list.ui.ListActivity
import com.todo.list.ui.ListContract
import dagger.Binds
import dagger.Module

@Module
abstract class ListViewModule {
  @Binds
  abstract fun provideListView(listActivity: ListActivity): ListContract.View
}