package com.todo.list.model.repository.model

sealed class NetworkState {
    object Loading : NetworkState()
    object Loaded : NetworkState()
    data class Error(val throwable: Throwable) : NetworkState()
}
