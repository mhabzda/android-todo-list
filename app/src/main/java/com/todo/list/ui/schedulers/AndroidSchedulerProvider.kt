package com.todo.list.ui.schedulers

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class AndroidSchedulerProvider @Inject constructor() : SchedulerProvider {
  override fun ui(): Scheduler {
    return AndroidSchedulers.mainThread()
  }

  override fun io(): Scheduler {
    return Schedulers.io()
  }
}