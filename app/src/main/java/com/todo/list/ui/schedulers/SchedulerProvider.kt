package com.todo.list.ui.schedulers

import io.reactivex.Scheduler

interface SchedulerProvider {
  fun ui(): Scheduler
  fun io(): Scheduler
}