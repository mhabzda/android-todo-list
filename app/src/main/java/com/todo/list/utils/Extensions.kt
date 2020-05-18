package com.todo.list.utils

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.todo.list.R
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

const val EMPTY = ""

fun DateTime.formatDateHour(): String {
  val formatter: DateTimeFormatter = DateTimeFormat.forPattern("dd.MM.yyyy HH:mm:ss")
  return formatter.print(this)
}

fun ImageView.loadImage(url: String?) {
  Glide.with(context)
    .load(url)
    .placeholder(R.drawable.common_google_signin_btn_icon_dark)
    .into(this)
}

@OptIn(ExperimentalContracts::class)
fun Any?.isNotNull(): Boolean {
  contract {
    returns(true) implies (this@isNotNull != null)
  }

  return this != null
}