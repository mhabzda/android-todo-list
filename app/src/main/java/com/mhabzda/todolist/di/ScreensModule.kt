package com.mhabzda.todolist.di

import com.mhabzda.todolist.di.annotation.ScreenScope
import com.mhabzda.todolist.di.list.ListViewModule
import com.mhabzda.todolist.ui.item.ItemFragment
import com.mhabzda.todolist.ui.list.ListFragment
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
