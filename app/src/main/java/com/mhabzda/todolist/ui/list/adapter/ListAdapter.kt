package com.mhabzda.todolist.ui.list.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mhabzda.todolist.databinding.ItemTodoBinding
import com.mhabzda.todolist.domain.model.TodoItem
import com.mhabzda.todolist.utils.format
import com.mhabzda.todolist.utils.loadImage
import javax.inject.Inject

class ListAdapter @Inject constructor(
    private val longClickAction: (String) -> Unit,
    private val clickAction: (String) -> Unit
) : PagingDataAdapter<TodoItem, ListAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTodoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemTodoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: TodoItem?) {
            item?.let {
                binding.root.setOnLongClickListener {
                    longClickAction.invoke(item.id)
                    true
                }
                binding.root.setOnClickListener { clickAction(item.id) }

                binding.itemImage.loadImage(it.iconUrl)
                binding.itemTitle.text = it.title
                binding.itemCreationDate.text = it.creationDateTime.format()
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<TodoItem>() {
            override fun areItemsTheSame(oldItem: TodoItem, newItem: TodoItem): Boolean {
                return oldItem.creationDateTime == newItem.creationDateTime
            }

            override fun areContentsTheSame(oldItem: TodoItem, newItem: TodoItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}
