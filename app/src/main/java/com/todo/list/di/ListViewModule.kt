package com.todo.list.di

import android.content.Context
import com.todo.list.ui.list.ListActivity
import com.todo.list.ui.list.ListContract
import com.todo.list.ui.list.ListRouter
import dagger.Binds
import dagger.Module

@Module
abstract class ListViewModule {
  @Binds
  abstract fun provideListView(listActivity: ListActivity): ListContract.View

  @Binds
  abstract fun provideListRouter(listRouter: ListRouter): ListContract.Router

  @Binds
  abstract fun provideNavigationContext(listActivity: ListActivity): Context
}