package nl.rvbsoftdev.curiosityreporting.global

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import nl.rvbsoftdev.curiosityreporting.data.Repository

/** SharedViewModel tied to navigationActivity LifeCycle (which hosts the nav fragment) to enable sharing of data between fragments **/

class SharedViewModel(app: Application) : AndroidViewModel(app) {

    /** Get most recent photos every time app launches. Uses random sol if mostRecentEarthPhotoDate is unavailable **/
    init {
        viewModelScope.launch {
            Repository.getRepository(app).apply {
                getLatestPhotos()
                getMostRecentDates()
            }
        }
    }

    val deletedAllFavorites = MutableLiveData(false)
}