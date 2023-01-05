package com.campbell.campbell.util

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.campbell.campbell.R

fun ImageView.downFromUrl(url: String?, progressDrawable: CircularProgressDrawable){

    val opt = RequestOptions()
        .placeholder(progressDrawable)
        .error(R.drawable.kamp_png)

    Glide.with(context)
        .setDefaultRequestOptions(opt)
        .load(url)
        .into(this)
}

fun ProgressBarr(context: Context) : CircularProgressDrawable{
return  CircularProgressDrawable(context).apply {
    strokeWidth = 8f
    centerRadius = 40f
    start()
}
}

fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}