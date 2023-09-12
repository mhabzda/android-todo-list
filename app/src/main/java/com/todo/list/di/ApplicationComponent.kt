package com.todo.list.di

import com.mhabzda.todolist.data.di.DataModule
import com.todo.list.App
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidInjectionModule::class,
        ScreensModule::class,
        DataModule::class,
        ViewModelsModule::class
    ]
)
interface ApplicationComponent : AndroidInjector<App> {
    companion object {
        fun create(): AndroidInjector<App> {
            return DaggerApplicationComponent.create()
        }
    }
}
