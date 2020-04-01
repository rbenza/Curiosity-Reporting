package nl.rvbsoftdev.curiosityreporting.global

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/** SharedViewModel tied to SingleActivity LifeCycle (which hosts the nav fragment) to enable sharing of data between fragments and provide global functions.**/

class SharedViewModel(application: Application) : AndroidViewModel(application) {

    /** Get most recent photos every time app launches. Uses random sol if mostRecentEarthPhotoDate is unavailable **/
    init {
        viewModelScope.launch {
            PhotoRepository.getRepository(application).apply {
                getMostRecentDates()
                if (!this.mostRecentEarthPhotoDate.value.isNullOrEmpty()) {
                    getPhotos(this.mostRecentEarthPhotoDate.value)
                } else {
                    getPhotos(null, Random().nextInt(2540))
                }
            }
        }
    }

    object DateFormatter {
        @SuppressLint("SimpleDateFormat")
        @JvmStatic
        fun formatDate(jsonDateInput: String?): String {
            val jsonDate = SimpleDateFormat("yyyy-MM-dd")
            val newDateFormat = SimpleDateFormat("EEEE, MMMM d yyyy", Locale.getDefault())
            val formattedDate: Date = jsonDate.parse(jsonDateInput)
            return newDateFormat.format(formattedDate)
        }
    }

    object DateFormatterCompact {
        @SuppressLint("SimpleDateFormat")
        @JvmStatic
        fun formatDate(jsonDateInput: String?): String {
            val jsonDate = SimpleDateFormat("yyyy-MM-dd")
            val newDateFormat = SimpleDateFormat("d MMM yyyy", Locale.getDefault())
            val formattedDate: Date = jsonDate.parse(jsonDateInput)
            return newDateFormat.format(formattedDate)
        }
    }

    object CalenderObjectProvider {
        @SuppressLint("SimpleDateFormat")
        @JvmStatic
        fun provideCalender(dateInput: String?): Calendar {
            val sdf = SimpleDateFormat("yyyy-MM-dd")
            val date: Date = sdf.parse(dateInput)
            val cal = Calendar.getInstance()
            cal.time = date
            return cal
        }
    }
}