package nl.rvbsoftdev.curiosityreporting.feature.explore

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import nl.rvbsoftdev.curiosityreporting.data.Photo
import nl.rvbsoftdev.curiosityreporting.data.NasaApiConnectionStatus
import nl.rvbsoftdev.curiosityreporting.data.PhotoRepository.Companion.getRepository

class ExploreViewModel(app: Application) : AndroidViewModel(app) {

    private val photoRepository = getRepository(app)

    val connectionStatus: LiveData<NasaApiConnectionStatus> = photoRepository.connectionStatus
    val photosFromNasaApi: LiveData<List<Photo>> = photoRepository.photosResultFromNasaApi
    val mostRecentSolPhotoDate: LiveData<Int> = photoRepository.mostRecentSolPhotoDate
    val mostRecentEarthPhotoDate: LiveData<String> = photoRepository.mostRecentEarthPhotoDate

    private val _cameraFilterStatus = MutableLiveData<String>()
    val cameraFilterStatus: LiveData<String>
    get() = _cameraFilterStatus

    fun refreshPhotos(earthDate: String? = null, sol: Int? = null, camera: String? = null) {
        viewModelScope.launch {
            photoRepository.getPhotos(earthDate, sol, camera)
        }
    }

    fun setCameraFilter(cameraFilter: String?) {
        try {
            refreshPhotos(photosFromNasaApi.value?.get(0)?.earth_date, null, cameraFilter)
            _cameraFilterStatus.value = "Succes"
        } catch (e: Exception) {
            _cameraFilterStatus.value = "Error"
        }
    }

}