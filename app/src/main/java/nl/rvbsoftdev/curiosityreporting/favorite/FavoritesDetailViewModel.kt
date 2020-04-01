package nl.rvbsoftdev.curiosityreporting.favorite

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import nl.rvbsoftdev.curiosityreporting.data.Photo
import nl.rvbsoftdev.curiosityreporting.global.PhotoRepository

class FavoritesDetailViewModel(photo: Photo, app: Application) : AndroidViewModel(app) {

    private val photoRepository = PhotoRepository.getRepository(getApplication())

    private val _selectedPhoto = MutableLiveData<Photo>()

    val selectedPhoto: LiveData<Photo>
        get() = _selectedPhoto

    init {
        _selectedPhoto.value = photo
    }

    fun removePhotoFromFavorites(photo: Photo) {
        viewModelScope.launch {
            photoRepository.removePhotoFromFavorites(photo)
        }
    }

}
