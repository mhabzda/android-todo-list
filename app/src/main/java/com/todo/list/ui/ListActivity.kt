package com.todo.list.ui

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.todo.list.R
import com.todo.list.di.ViewModelFactory
import com.todo.list.model.entities.TodoItem
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_list.main_text
import javax.inject.Inject
import kotlinx.android.synthetic.main.activity_list.my_toolbar as toolbar

class ListActivity : DaggerAppCompatActivity(), ListContract.View {
  @Inject
  lateinit var viewModelFactory: ViewModelFactory<ListPresenter>

  lateinit var presenter: ListContract.Presenter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_list)
    setActionBar(toolbar)

    presenter = ViewModelProvider(this, viewModelFactory).get(ListPresenter::class.java)
    presenter.fetchItems()
  }

  override fun displayTodoList(items: List<TodoItem>) {
    main_text.text = items.toString()
  }
}
