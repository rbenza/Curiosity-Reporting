package nl.rvbsoftdev.curiosityreporting.feature.favorite

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import nl.rvbsoftdev.curiosityreporting.data.Photo
import nl.rvbsoftdev.curiosityreporting.data.Repository

class FavoritesDetailViewModel(app: Application) : AndroidViewModel(app) {

    private val photoRepository = Repository.getRepository(app)
    val selectedPhoto = MutableLiveData<Photo>()

    fun removePhotoFromFavorites(photo: Photo) {
        viewModelScope.launch {
            photoRepository.removePhotoFromDatabase(photo)
        }
    }
}
