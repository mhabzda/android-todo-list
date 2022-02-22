package com.todo.list.ui.parcel

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TodoItemParcelable(
    val title: String,
    val description: String,
    val creationDate: String,
    val iconUrl: String?
) : Parcelable
