package com.mhabzda.todolist.base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class BaseViewModel<State, Effect>(
    defaultState: State,
    private val emitSnackbarMessage: suspend (String) -> Unit = {},
) : ViewModel() {
    private val stateFlow = MutableStateFlow(defaultState)
    val state = stateFlow.asStateFlow()

    private val effectsFlow = MutableSharedFlow<Effect>()
    val effects = effectsFlow.asSharedFlow()

    protected fun updateState(block: State.() -> State) {
        stateFlow.value = block(stateFlow.value)
    }

    protected suspend fun sendEffect(effect: Effect) {
        effectsFlow.emit(effect)
    }

    suspend fun showSnackbar(message: String) {
        emitSnackbarMessage(message)
    }
}
