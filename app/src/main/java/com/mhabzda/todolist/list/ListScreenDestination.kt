package com.mhabzda.todolist.list

import androidx.navigation.NamedNavArgument
import com.mhabzda.todolist.Destination

data object ListScreenDestination : Destination() {
    override val name: String = "list"
    override fun getDirection(vararg argument: Any?) = name
    override fun getNavArguments(): List<NamedNavArgument> = emptyList()
}