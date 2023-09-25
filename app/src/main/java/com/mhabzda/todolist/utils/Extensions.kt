package com.mhabzda.todolist.utils

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

fun ZonedDateTime.format(): String =
    format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))

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
