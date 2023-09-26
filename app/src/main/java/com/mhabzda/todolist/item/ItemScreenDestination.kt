package com.mhabzda.todolist.item

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.mhabzda.todolist.Destination

data object ItemScreenDestination : Destination() {

    const val ITEM_ID_ARG_NAME = "itemId"

    override val name: String = "item/?$ITEM_ID_ARG_NAME={$ITEM_ID_ARG_NAME}"

    override fun getDirection(vararg argument: Any?): String {
        val itemId = argument.firstOrNull() as? String
        return itemId?.let { name.replace("{$ITEM_ID_ARG_NAME}", it) } ?: name
    }

    override fun getNavArguments(): List<NamedNavArgument> =
        listOf(
            navArgument(ITEM_ID_ARG_NAME) {
                type = NavType.StringType
                nullable = true
            },
        )
}