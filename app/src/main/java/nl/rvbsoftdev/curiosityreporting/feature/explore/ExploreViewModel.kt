package nl.rvbsoftdev.curiosityreporting.feature.explore

import android.app.Application
import android.graphics.drawable.Drawable
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import nl.rvbsoftdev.curiosityreporting.R
import nl.rvbsoftdev.curiosityreporting.data.NasaApiConnectionStatus
import nl.rvbsoftdev.curiosityreporting.data.Photo
import nl.rvbsoftdev.curiosityreporting.data.Repository.Companion.getRepository
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import java.util.*

class ExploreViewModel(private val app: Application) : AndroidViewModel(app) {

    private val photoRepository = getRepository(app)

    val connectionStatus: LiveData<NasaApiConnectionStatus> = photoRepository.connectionStatus
    val photosFromNasaApi: LiveData<List<Photo>> = photoRepository.photosFromNasaApi
    val mostRecentSolPhotoDate: LiveData<Int> = photoRepository.mostRecentSolPhotoDate
    val mostRecentEarthPhotoDate: LiveData<String?> = photoRepository.mostRecentEarthPhotoDate
    val selectedPhoto = MutableLiveData<Photo>()
    val filteredPhotosFromNasaApi = MutableLiveData<List<Photo>>()

    fun getPhotos(earthDate: String? = null, sol: Int? = null, camera: String? = null) {
        viewModelScope.launch {
            photoRepository.getPhotosWithSolOrEathDate(earthDate, sol, camera)
        }
    }

    fun setCameraFilter(cameraFilter: String?) {
        filteredPhotosFromNasaApi.value = photosFromNasaApi.value
        filteredPhotosFromNasaApi.value = filteredPhotosFromNasaApi.value?.filter { photo ->
            photo.camera?.name == cameraFilter
        }
    }

    val iconConnectionStatus: LiveData<Drawable?> = connectionStatus.map {
        when (it) {
            NasaApiConnectionStatus.NO_DATA -> ResourcesCompat.getDrawable(app.resources, R.drawable.icon_database_no_data, null)
            else -> ResourcesCompat.getDrawable(app.resources, R.drawable.icon_connection_error, null)
        }
    }

    val textConnectionStatus: LiveData<String> = connectionStatus.map {
        when (it) {
            NasaApiConnectionStatus.LOADING -> app.getString(R.string.connecting_nasa_db)
            NasaApiConnectionStatus.NO_DATA -> app.getString(R.string.no_photos_in_nasa_db)
            NasaApiConnectionStatus.ERROR -> app.getString(R.string.no_conn_nasa_db)
            else -> ""
        }
    }

    fun toggleFavorite(photo: Photo) {
        viewModelScope.launch {
            if (selectedPhoto.value?.isFavorite == false) {
                photoRepository.addPhotoToDatabase(photo)
                selectedPhoto.value?.isFavorite = true
            } else {
                photoRepository.removePhotoFromDatabase(photo)
                selectedPhoto.value?.isFavorite = false
            }
        }
    }

    fun formatStringDate(input: String?): String {
        if (input == null) return ""
        val toLocalDate = LocalDate.parse(input, DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault()))
        return DateTimeFormatter.ofPattern("d MMM yyyy").format(toLocalDate)
    }
}

