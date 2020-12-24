package nl.rvbsoftdev.curiosityreporting.global

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import nl.rvbsoftdev.curiosityreporting.data.Photo

/** SharedViewModel tied to navigationActivity LifeCycle (which hosts the nav fragment) to enable sharing of data between fragments **/

class SharedViewModel : ViewModel() {

    val deletedAllFavorites = MutableLiveData(false)

    val deletedFavoritePhoto = MutableLiveData<Photo>()
}