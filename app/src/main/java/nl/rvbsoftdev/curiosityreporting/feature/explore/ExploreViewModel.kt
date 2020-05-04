package nl.rvbsoftdev.curiosityreporting.feature.explore

import android.app.Application
import android.graphics.drawable.Drawable
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import nl.rvbsoftdev.curiosityreporting.R
import nl.rvbsoftdev.curiosityreporting.data.Photo
import nl.rvbsoftdev.curiosityreporting.data.NasaApiConnectionStatus
import nl.rvbsoftdev.curiosityreporting.data.PhotoRepository.Companion.getRepository

class ExploreViewModel(private val app: Application) : AndroidViewModel(app) {

    private val photoRepository = getRepository(app)

    val connectionStatus: LiveData<NasaApiConnectionStatus> = photoRepository.connectionStatus
    var photosFromNasaApi: MutableLiveData<List<Photo>> = photoRepository.photosFromNasaApi
    val photosFromNasaApiBeforeFiltering = photoRepository.photosFromNasaApi
    val mostRecentSolPhotoDate: LiveData<Int> = photoRepository.mostRecentSolPhotoDate
    val mostRecentEarthPhotoDate: LiveData<String> = photoRepository.mostRecentEarthPhotoDate

    fun refreshPhotos(earthDate: String? = null, sol: Int? = null, camera: String? = null) {
        viewModelScope.launch {
            photoRepository.getPhotos(earthDate, sol, camera)
        }
    }

    fun setCameraFilter(cameraFilter: String?) {
       photosFromNasaApi.value = photosFromNasaApi.value?.filter { photo ->
           photo.camera.name == cameraFilter
        }
    }

    val iconConnectionStatus: LiveData<Drawable> = connectionStatus.map {
        when (it) {
            NasaApiConnectionStatus.NODATA -> app.resources.getDrawable(R.drawable.icon_database_no_data, null)
            NasaApiConnectionStatus.ERROR -> app.resources.getDrawable(R.drawable.icon_connection_error, null)
            NasaApiConnectionStatus.LOADING -> app.resources.getDrawable(R.drawable.icon_connection_error, null)
            else -> app.resources.getDrawable(R.drawable.icon_connection_error, null)
        }
    }

    val textConnectionStatus: LiveData<String> = connectionStatus.map {
        when (it) {
            NasaApiConnectionStatus.LOADING -> app.getString(R.string.connecting_nasa_db)
            NasaApiConnectionStatus.NODATA -> app.getString(R.string.no_photos_in_nasa_db)
            NasaApiConnectionStatus.ERROR -> app.getString(R.string.no_conn_nasa_db)
            else -> ""
        }
    }
}

