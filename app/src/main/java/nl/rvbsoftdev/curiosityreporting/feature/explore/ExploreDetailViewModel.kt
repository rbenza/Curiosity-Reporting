package nl.rvbsoftdev.curiosityreporting.feature.explore

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import nl.rvbsoftdev.curiosityreporting.data.Photo
import nl.rvbsoftdev.curiosityreporting.data.PhotoRepository.Companion.getRepository

class ExploreDetailViewModel(app: Application) : AndroidViewModel(app) {

    private val photoRepository = getRepository(app)
    val selectedPhoto = MutableLiveData<Photo>()

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
}