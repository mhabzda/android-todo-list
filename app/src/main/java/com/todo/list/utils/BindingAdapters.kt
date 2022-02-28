package com.todo.list.utils

import android.view.View
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter

@BindingAdapter("isVisible")
fun View.bindIsVisible(value: Boolean) {
    isVisible = value
}
