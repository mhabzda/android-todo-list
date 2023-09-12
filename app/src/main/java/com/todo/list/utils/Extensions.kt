package com.todo.list.utils

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager
import com.todo.list.R
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

fun ZonedDateTime.format(): String =
    format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))

fun ImageView.loadImage(url: String?) {
    Glide.with(context)
        .loadImageIfUrlIsPresent(url)
        .into(this)
}

fun RequestManager.loadImageIfUrlIsPresent(url: String?): RequestBuilder<Drawable> {
    val placeholder = R.drawable.ic_check_box
    return if (url.isNullOrEmpty()) {
        load(placeholder)
    } else {
        load(url).placeholder(placeholder)
    }
}

@OptIn(ExperimentalContracts::class)
fun Any?.isNotNull(): Boolean {
    contract {
        returns(true) implies (this@isNotNull != null)
    }

    return this != null
}

fun <T> Result<T>.onTerminate(block: () -> Unit) {
    onSuccess { block.invoke() }
    onFailure { block.invoke() }
}

inline fun <reified T> Flow<T>.observeWhenStarted(
    lifecycleOwner: LifecycleOwner,
    noinline action: suspend (T) -> Unit
): Job = lifecycleOwner.lifecycleScope.launch {
    flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED).collectLatest(action)
}
