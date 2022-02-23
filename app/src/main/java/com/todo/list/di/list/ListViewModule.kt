package com.todo.list.di.list

import android.content.Context
import com.todo.list.ui.list.ListActivity
import com.todo.list.ui.list.navigation.ListRouter
import com.todo.list.ui.list.navigation.ListRouterImpl
import dagger.Binds
import dagger.Module

@Module
abstract class ListViewModule {
    @Binds
    abstract fun provideListRouter(listRouter: ListRouterImpl): ListRouter

    @Binds
    abstract fun provideNavigationContext(listActivity: ListActivity): Context
}
