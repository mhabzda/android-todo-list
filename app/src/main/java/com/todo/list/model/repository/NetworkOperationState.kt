package com.todo.list.model.repository

sealed class NetworkOperationState {
  object Loading : NetworkOperationState()
  object Loaded : NetworkOperationState()
  data class Error(val throwable: Throwable) : NetworkOperationState()
}