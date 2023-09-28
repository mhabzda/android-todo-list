package com.mhabzda.todolist.item.mapper

import com.mhabzda.todolist.R
import com.mhabzda.todolist.item.mode.ItemScreenMode
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ItemConfirmationMessageMapperTest {

    private val mapper = ItemConfirmationMessageMapper()

    @Test
    fun `GIVEN create mode WHEN map THEN return correct text`() {
        val result = mapper.map(ItemScreenMode.CREATE)

        assertEquals(R.string.item_create_confirmation_message, result)
    }

    @Test
    fun `GIVEN edit mode WHEN map THEN return correct text`() {
        val result = mapper.map(ItemScreenMode.EDIT)

        assertEquals(R.string.item_edit_confirmation_message, result)
    }
}
