package nl.rvbsoftdev.curiosityreporting.feature.explore

import android.app.Application
import android.content.Context
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import nl.rvbsoftdev.curiosityreporting.R
import nl.rvbsoftdev.curiosityreporting.data.NetworkRequestState
import nl.rvbsoftdev.curiosityreporting.data.Photo
import nl.rvbsoftdev.curiosityreporting.data.Repository.Companion.getRepository
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import java.util.*

class ExploreViewModel(private val app: Application) : AndroidViewModel(app) {

    private val photoRepository = getRepository(app)

    sealed class CombinedConnectionState {
        object Idle : CombinedConnectionState()
        object Loading : CombinedConnectionState()
        object NoData : CombinedConnectionState()
        object ConnectionError : CombinedConnectionState()
        object Offline : CombinedConnectionState()
    }

    private val networkRequestState: LiveData<NetworkRequestState> = photoRepository.networkRequestState.asLiveData()

    private val isOnline = MutableLiveData<Boolean>()

    // Combines the network requesting state and checks if the device is online
    val combinedConnectionState: LiveData<CombinedConnectionState> = networkRequestState.switchMap { connectionStatus ->
        isOnline.map { isOnline ->
            when  {
                connectionStatus == NetworkRequestState.IDLE && isOnline-> CombinedConnectionState.Idle
                connectionStatus == NetworkRequestState.LOADING && isOnline -> CombinedConnectionState.Loading
                connectionStatus == NetworkRequestState.NO_DATA && isOnline -> CombinedConnectionState.NoData
                connectionStatus == NetworkRequestState.CONNECTION_ERROR && isOnline -> CombinedConnectionState.ConnectionError
                connectionStatus == NetworkRequestState.CONNECTION_ERROR && !isOnline -> CombinedConnectionState.Offline
                else -> CombinedConnectionState.Offline
            }
        }
    }

    private val _photos = MutableLiveData<List<Photo>>()
    val photos: LiveData<List<Photo>> = _photos

    val mostRecentSolPhotoDate: Int? get() = photoRepository.mostRecentSolPhotoDate
    val mostRecentEarthPhotoDate: String? get() = photoRepository.mostRecentEarthPhotoDate

    private val _selectedPhoto = MutableLiveData<Photo?>(null)
    val selectedPhoto: LiveData<Photo?> = _selectedPhoto

    private val _filteredPhotos = MutableLiveData<List<Photo>?>()
    val filteredPhotos: LiveData<List<Photo>?> = _filteredPhotos

    init {
        isOnline.value = isOnline()
        viewModelScope.launch {
            if (isOnline()) {
                photoRepository.getMostRecentDates()

                if (_photos.value.isNullOrEmpty()) {
                    _photos.value = photoRepository.getLatestPhotos()
                }
            }
        }
    }

    fun getMostRecentDates() {
        viewModelScope.launch {
            if (isOnline()) {
                photoRepository.getMostRecentDates()
            }
        }
    }

    fun getPhotos(earthDate: String? = null, sol: Int? = null, camera: String? = null) {
        viewModelScope.launch {
            if (isOnline()) {
                _photos.value = photoRepository.getPhotosWithSolOrEathDate(earthDate, sol, camera)
            }
        }
    }

    fun setCameraFilter(cameraFilter: String?) {
        _filteredPhotos.value = photos.value
        _filteredPhotos.value = _filteredPhotos.value?.filter { photo ->
            photo.camera?.name == cameraFilter
        }
    }

    fun resetPhotoFilter() {
        _filteredPhotos.value = null
    }

    fun deleteAllFavorites() {
        _photos.value = _photos.value?.onEach { it.isFavorite = false }
    }

    fun setSelectedPhoto(photo: Photo?) {
        _selectedPhoto.value = photo
    }

    // called when user removes a photo in FavoriteFragment, communication through the shared viewmodel
    fun removedPhotoFromFavorites(photo: Photo) {
        _photos.value = _photos.value?.onEach { if (it.id == photo.id) it.isFavorite = false }
    }

    val iconConnectionState: LiveData<Int> = combinedConnectionState.map {
        when (it) {
            CombinedConnectionState.NoData -> R.drawable.icon_database_no_data
            CombinedConnectionState.ConnectionError -> R.drawable.icon_connection_error
            else -> R.drawable.icon_offline
        }
    }

    val textConnectionState: LiveData<String> = combinedConnectionState.map {
        when (it) {
            CombinedConnectionState.Idle -> ""
            CombinedConnectionState.Loading -> app.getString(R.string.connecting_nasa_db)
            CombinedConnectionState.NoData -> app.getString(R.string.no_photos_in_nasa_db)
            CombinedConnectionState.ConnectionError -> app.getString(R.string.no_conn_nasa_db)
            CombinedConnectionState.Offline -> app.getString(R.string.offline)
        }
    }

    fun toggleFavorite(photo: Photo) {
        viewModelScope.launch {
            if (selectedPhoto.value?.isFavorite == false) {
                photoRepository.addPhotoToDatabase(photo)
                selectedPhoto.value?.isFavorite = true
                _photos.value = _photos.value?.onEach { if (it.id == photo.id) it.isFavorite = true }
            } else {
                photoRepository.removePhotoFromDatabase(photo)
                selectedPhoto.value?.isFavorite = false
                _photos.value = _photos.value?.onEach { if (it.id == photo.id) it.isFavorite = false }
            }
        }
    }

    fun formatStringDate(input: String?): String {
        if (input == null) return ""
        val toLocalDate = LocalDate.parse(input, DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault()))
        return DateTimeFormatter.ofPattern("d MMM yyyy").format(toLocalDate)
    }


    private fun isOnline(): Boolean {
        val cm = app.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        isOnline.value = activeNetwork?.isConnectedOrConnecting == true
        return activeNetwork?.isConnectedOrConnecting == true
    }
}

