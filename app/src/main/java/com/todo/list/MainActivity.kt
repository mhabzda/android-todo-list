package com.todo.list

import android.os.Bundle
import android.util.Log
import com.todo.list.model.repository.TodoRepository
import dagger.android.DaggerActivity
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.main_text
import javax.inject.Inject
import kotlinx.android.synthetic.main.activity_main.my_toolbar as toolbar

class MainActivity : DaggerActivity() {
  @Inject
  lateinit var todoRepository: TodoRepository

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    setActionBar(toolbar)

    todoRepository.getTodoItems()
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribeBy(
        onSuccess = {
          main_text.text = it.toString()
        },
        onError = {
          Log.e(MainActivity::class.simpleName, "Error", it)
        }
      )
  }
}
