package nl.rvbsoftdev.curiosityreporting.feature.explore

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import nl.rvbsoftdev.curiosityreporting.data.Photo
import nl.rvbsoftdev.curiosityreporting.data.PhotoRepository.Companion.getRepository

class ExploreDetailViewModel(val app: Application) : AndroidViewModel(app) {

    private val photoRepository = getRepository(app)
    val selectedPhoto = MutableLiveData<Photo>()
    val isFavorite = MutableLiveData<Boolean>()

    init {
        isFavorite.value = selectedPhoto.value?.isFavorite
    }

    fun addPhotoToFavorites(photo: Photo) {
        isFavorite.value = selectedPhoto.value?.isFavorite
        viewModelScope.launch {
            photoRepository.addPhotoToDatabase(photo)
        }
    }

    fun removePhotoFromFavorites(photo: Photo) {
        isFavorite.value = selectedPhoto.value?.isFavorite
        viewModelScope.launch {
            photoRepository.removePhotoFromDatabase(photo)
        }
    }
}