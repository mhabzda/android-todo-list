package com.todo.list.di

import com.todo.list.ui.ListActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ScreensModule {
  @ContributesAndroidInjector
  abstract fun provideListActivity(): ListActivity
}