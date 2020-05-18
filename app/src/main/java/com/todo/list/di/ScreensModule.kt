package com.todo.list.di

import com.todo.list.ui.list.ListActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ScreensModule {
  @ContributesAndroidInjector(modules = [ListViewModule::class])
  abstract fun provideListActivity(): ListActivity
}