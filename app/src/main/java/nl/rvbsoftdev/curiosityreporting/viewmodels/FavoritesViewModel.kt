package nl.rvbsoftdev.curiosityreporting.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import nl.rvbsoftdev.curiosityreporting.domain.Photo
import nl.rvbsoftdev.curiosityreporting.repository.PhotoRepository.Companion.getRepository

class FavoritesViewModel(app: Application) : AndroidViewModel(app) {

    private val photoRepository = getRepository(getApplication())

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



