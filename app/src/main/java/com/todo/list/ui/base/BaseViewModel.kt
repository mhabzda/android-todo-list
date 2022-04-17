package com.todo.list.ui.base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow

abstract class BaseViewModel<State, Event>(defaultState: State) : ViewModel() {
    private val stateFlow = MutableStateFlow(defaultState)
    val state = stateFlow.asStateFlow()

    private val eventChannel = Channel<Event>()
    val events = eventChannel.receiveAsFlow()

    protected fun updateState(block: State.() -> State) {
        stateFlow.value = block(stateFlow.value)
    }

    protected suspend fun sendEvent(action: Event) {
        eventChannel.send(action)
    }
}
