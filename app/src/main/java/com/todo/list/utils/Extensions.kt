package com.todo.list.utils

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Parcelable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager
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
    .loadImageIfUrlIsPresent(url)
    .into(this)
}

fun RequestManager.loadImageIfUrlIsPresent(url: String?): RequestBuilder<Drawable> {
  val placeholder = R.drawable.ic_check_box_100
  return if (url.isNullOrEmpty()) {
    load(placeholder)
  } else {
    load(url).placeholder(placeholder)
  }
}

@OptIn(ExperimentalContracts::class)
fun Any?.isNotNull(): Boolean {
  contract {
    returns(true) implies (this@isNotNull != null)
  }

  return this != null
}

fun <T : Parcelable> Intent.getParcelableStrictly(key: String): T {
  getParcelableExtra<T>(key)?.let {
    return it
  } ?: throw NullPointerException()
}