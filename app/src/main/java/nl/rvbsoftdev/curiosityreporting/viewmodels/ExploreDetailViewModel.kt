package nl.rvbsoftdev.curiosityreporting.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import nl.rvbsoftdev.curiosityreporting.domain.Photo
import nl.rvbsoftdev.curiosityreporting.repository.PhotoRepository.Companion.getRepository

class ExploreDetailViewModel(photo: Photo, app: Application) : AndroidViewModel(app) {

    private val photoRepository = getRepository(getApplication())

    private val _selectedPhoto = MutableLiveData<Photo>()

    val selectedPhoto: LiveData<Photo>
        get() = _selectedPhoto

    init {
        _selectedPhoto.value = photo
    }


    fun addPhotoToFavorites(photo: Photo) {
        viewModelScope.launch {
            photoRepository.addPhotoToFavorites(photo)
        }
    }

    fun removePhotoFromFavorites(photo: Photo) {
        viewModelScope.launch {
            photoRepository.removePhotoFromFavorites(photo)
        }
    }

    fun searchForPhotoInFavoritesDatabase(photo: Photo): LiveData<Photo>? {
        var searchResult: LiveData<Photo>? = null
        viewModelScope.launch {
            searchResult = photoRepository.searchForPhotoInFavoritesDatabase(photo)
        }
        return searchResult
    }
}



