package com.todo.list.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.todo.list.R
import com.todo.list.model.entities.TodoItem
import com.todo.list.utils.formatDateHour
import com.todo.list.utils.loadImage
import kotlinx.android.extensions.LayoutContainer
import javax.inject.Inject
import kotlinx.android.synthetic.main.item_todo.item_creation_date as creationTimeView
import kotlinx.android.synthetic.main.item_todo.item_image as imageIconView
import kotlinx.android.synthetic.main.item_todo.item_title as titleView

class ListAdapter @Inject constructor() : PagedListAdapter<TodoItem, ListAdapter.ViewHolder>(DIFF_CALLBACK) {
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val inflater = LayoutInflater.from(parent.context)
    val view = inflater.inflate(R.layout.item_todo, parent, false)

    return ViewHolder(view)
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.bind(getItem(position))
  }

  inner class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {
    fun bind(item: TodoItem?) {
      item?.let {
        titleView.text = it.title
        creationTimeView.text = it.creationDate.formatDateHour()
        imageIconView.loadImage(it.iconUrl)
      }
    }
  }

  companion object {
    private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<TodoItem>() {
      override fun areItemsTheSame(oldItem: TodoItem, newItem: TodoItem): Boolean {
        return oldItem.title == newItem.title
      }

      override fun areContentsTheSame(oldItem: TodoItem, newItem: TodoItem): Boolean {
        return oldItem == newItem
      }

    }
  }
}