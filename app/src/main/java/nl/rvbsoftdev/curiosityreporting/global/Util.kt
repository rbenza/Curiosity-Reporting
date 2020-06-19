package nl.rvbsoftdev.curiosityreporting.global

import android.view.View
import android.view.ViewGroup
import java.text.SimpleDateFormat
import java.util.*


/** Some convenient extension functions on the View class **/

fun View.setVisible() {
    visibility = View.VISIBLE
}

fun View.setInvisible() {
    visibility = View.INVISIBLE
}

fun View.setGone() {
    visibility = View.GONE
}

fun View?.removeSelf() {
    this ?: return
    val parentView = parent as? ViewGroup ?: return
    parentView.removeView(this)
}

fun View.viewVisibleOrGone(show: Boolean) = if (show) setVisible() else setGone()

