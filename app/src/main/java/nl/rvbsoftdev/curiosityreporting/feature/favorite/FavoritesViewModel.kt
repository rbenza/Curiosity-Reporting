package nl.rvbsoftdev.curiosityreporting.feature.favorite

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import nl.rvbsoftdev.curiosityreporting.data.Photo
import nl.rvbsoftdev.curiosityreporting.data.PhotoRepository.Companion.getRepository

class FavoritesViewModel(app: Application) : AndroidViewModel(app) {

    private val photoRepository = getRepository(app)

    /** Get all favorite photos from Room database through repository (uses suspend function)**/
    init {
        viewModelScope.launch {
            photoRepository.getAllFavoritesPhotos()
        }
    }

    val favoritePhotos = photoRepository.favoritePhotos

    private val _selectedFavoritePhoto = MutableLiveData<Photo>()

    val selectedPhoto: LiveData<Photo>
        get() = _selectedFavoritePhoto

    private val _navigateToSelectedFavoritePhoto = MutableLiveData<Photo>()

    val navigateToSelectedPhoto: LiveData<Photo>
        get() = _navigateToSelectedFavoritePhoto


    fun displayFavoritePhotoDetails(photo: Photo) {
        _navigateToSelectedFavoritePhoto.value = photo
    }

    fun displayFavoritePhotoDetailsFinished() {
        _navigateToSelectedFavoritePhoto.value = null
    }

    fun removeAllPhotoFromFavorites() {
        viewModelScope.launch {
            photoRepository.deleteAllFavoritesPhotos()
        }
    }
}



