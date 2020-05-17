package com.todo.list.ui.schedulers

import io.reactivex.rxjava3.core.Scheduler

interface SchedulerProvider {
  fun ui(): Scheduler
  fun io(): Scheduler
}