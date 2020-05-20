package com.todo.list.testutilities

import com.todo.list.ui.schedulers.SchedulerProvider
import io.reactivex.Scheduler
import io.reactivex.schedulers.TestScheduler

class TestSchedulerProvider : SchedulerProvider {
  private val testScheduler = TestScheduler()

  override fun ui(): Scheduler {
    return testScheduler
  }

  override fun io(): Scheduler {
    return testScheduler
  }

  fun triggerActions() {
    testScheduler.triggerActions()
  }
}