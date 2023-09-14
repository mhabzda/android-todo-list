package com.mhabzda.todolist

import com.mhabzda.todolist.di.ApplicationComponent
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication

class App : DaggerApplication() {
    override fun applicationInjector(): AndroidInjector<App> {
        return ApplicationComponent.create()
    }
}
