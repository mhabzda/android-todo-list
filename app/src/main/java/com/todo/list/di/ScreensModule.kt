package com.todo.list.di

import com.todo.list.di.annotation.ActivityScope
import com.todo.list.ui.item.creation.ItemCreationActivity
import com.todo.list.ui.item.edition.ItemEditionActivity
import com.todo.list.ui.list.ListActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ScreensModule {
  @ActivityScope
  @ContributesAndroidInjector(modules = [ListViewModule::class])
  abstract fun provideListActivity(): ListActivity

  @ActivityScope
  @ContributesAndroidInjector(modules = [ItemCreationViewModule::class])
  abstract fun provideItemCreationActivity(): ItemCreationActivity

  @ActivityScope
  @ContributesAndroidInjector(modules = [ItemEditionViewModule::class])
  abstract fun provideItemEditionActivity(): ItemEditionActivity
}