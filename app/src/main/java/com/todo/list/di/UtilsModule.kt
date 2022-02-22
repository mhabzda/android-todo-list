package com.todo.list.di

import com.todo.list.ui.schedulers.AndroidSchedulerProvider
import com.todo.list.ui.schedulers.SchedulerProvider
import dagger.Binds
import dagger.Module

@Module
abstract class UtilsModule {
    @Binds
    abstract fun provideSchedulerProvider(schedulerProvider: AndroidSchedulerProvider): SchedulerProvider
}
