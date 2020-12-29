package nl.rvbsoftdev.curiosityreporting.global

import android.view.View
import android.view.ViewGroup


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
