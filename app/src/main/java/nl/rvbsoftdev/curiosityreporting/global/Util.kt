package nl.rvbsoftdev.curiosityreporting.global

import android.view.View
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

fun View.viewVisibleOrGone(show: Boolean) = if (show) setVisible() else setGone()

/** DateFormatting functions **/

fun formatDate(jsonDateInput: String?): String {
    val jsonDate = SimpleDateFormat("yyyy-MM-dd")
    val newDateFormat = SimpleDateFormat("EEEE, MMMM d yyyy", Locale.getDefault())
    val formattedDate: Date = jsonDate.parse(jsonDateInput)
    return newDateFormat.format(formattedDate)
}


fun formatDateCompact(jsonDateInput: String?): String {
    val jsonDate = SimpleDateFormat("yyyy-MM-dd")
    val newDateFormat = SimpleDateFormat("d MMM yyyy", Locale.getDefault())
    val formattedDate: Date = jsonDate.parse(jsonDateInput)
    return newDateFormat.format(formattedDate)
}


fun provideCalender(dateInput: String?): Calendar {
    val sdf = SimpleDateFormat("yyyy-MM-dd")
    val date: Date = sdf.parse(dateInput)
    val cal = Calendar.getInstance()
    cal.time = date
    return cal
}
