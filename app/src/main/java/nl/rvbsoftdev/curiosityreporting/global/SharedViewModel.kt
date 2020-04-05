package nl.rvbsoftdev.curiosityreporting.global

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import nl.rvbsoftdev.curiosityreporting.data.PhotoRepository
import java.util.*

/** SharedViewModel tied to navigationActivity LifeCycle (which hosts the nav fragment) to enable sharing of data between fragments **/

class SharedViewModel(application: Application) : AndroidViewModel(application) {

    /** Get most recent photos every time app launches. Uses random sol if mostRecentEarthPhotoDate is unavailable **/
    init {
        viewModelScope.launch {
            PhotoRepository.getRepository(application).apply {
                getMostRecentDates()
                if (!mostRecentEarthPhotoDate.value.isNullOrEmpty()) {
                    getPhotos(mostRecentEarthPhotoDate.value)
                } else {
                    getPhotos(null, Random().nextInt(2580))
                }
            }
        }
    }
}