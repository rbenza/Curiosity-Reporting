package nl.rvbsoftdev.curiosityreporting.global

import android.app.Application
import androidx.lifecycle.AndroidViewModel

/** SharedViewModel tied to navigationActivity LifeCycle (which hosts the nav fragment) to enable sharing of data between fragments **/

class SharedViewModel(application: Application) : AndroidViewModel(application) {

    /** Get most recent photos every time app launches. Uses random sol if mostRecentEarthPhotoDate is unavailable **/
//    init {
//        viewModelScope.launch {
//            PhotoRepository.apply {
//                getMostRecentDates()
//                if (!mostRecentEarthPhotoDate.value.isNullOrEmpty()) {
//                    getPhotos(mostRecentEarthPhotoDate.value)
//                } else {
//                    getPhotos(null, Random().nextInt(2580))
//                }
//            }
//        }
//    }
}