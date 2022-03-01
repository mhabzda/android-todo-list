package com.todo.list.di

import com.todo.list.di.annotation.ScreenScope
import com.todo.list.di.list.ListViewModule
import com.todo.list.ui.item.ItemFragment
import com.todo.list.ui.list.ListFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ScreensModule {
    @ScreenScope
    @ContributesAndroidInjector(modules = [ListViewModule::class])
    abstract fun provideListFragment(): ListFragment

    @ScreenScope
    @ContributesAndroidInjector
    abstract fun provideItemFragment(): ItemFragment
}
