package nl.rvbsoftdev.curiosityreporting.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.*
import nl.rvbsoftdev.curiosityreporting.domain.Photo
import nl.rvbsoftdev.curiosityreporting.repository.NasaApiConnectionStatus
import nl.rvbsoftdev.curiosityreporting.repository.PhotoRepository.Companion.getRepository

class ExploreViewModel(app: Application) : AndroidViewModel(app) {

    private val photoRepository = getRepository(getApplication())

    val connectionStatus: LiveData<NasaApiConnectionStatus> = photoRepository.connectionStatus

    var photosFromNasaApi: LiveData<List<Photo>> = photoRepository.photosResultFromNasaApi

    val mostRecentSolPhotoDate: LiveData<Int> = photoRepository.mostRecentSolPhotoDate

    val mostRecentEarthPhotoDate: LiveData<String> = photoRepository.mostRecentEarthPhotoDate

    private val _navigateToSelectedPhoto = MutableLiveData<Photo>()

    private val _cameraFilterStatus = MutableLiveData<String>()

    val cameraFilterStatus: LiveData<String>
    get() = _cameraFilterStatus

    val navigateToSelectedPhoto: LiveData<Photo>
        get() = _navigateToSelectedPhoto

    fun displayPhotoDetails(photo: Photo) {
        _navigateToSelectedPhoto.value = photo
    }

    fun displayPhotoDetailsFinished() {
        _navigateToSelectedPhoto.value = null
    }

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




