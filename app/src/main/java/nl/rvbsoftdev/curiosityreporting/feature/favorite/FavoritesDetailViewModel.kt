package nl.rvbsoftdev.curiosityreporting.feature.favorite

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import nl.rvbsoftdev.curiosityreporting.data.Photo
import nl.rvbsoftdev.curiosityreporting.data.PhotoRepository

class FavoritesDetailViewModel(app: Application) : AndroidViewModel(app) {

    private val photoRepository = PhotoRepository.getRepository(app)
    val selectedPhoto = MutableLiveData<Photo>()

    fun removePhotoFromFavorites(photo: Photo) {
        viewModelScope.launch {
            photoRepository.removePhotoFromDatabase(photo)
        }
    }
}
