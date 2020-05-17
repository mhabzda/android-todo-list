package com.todo.list

import com.todo.list.di.ApplicationComponent
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication

class App : DaggerApplication() {
  override fun applicationInjector(): AndroidInjector<App> {
    return ApplicationComponent.create()
  }
}