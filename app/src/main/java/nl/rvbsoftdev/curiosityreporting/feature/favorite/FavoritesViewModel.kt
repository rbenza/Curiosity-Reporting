package nl.rvbsoftdev.curiosityreporting.feature.favorite

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import nl.rvbsoftdev.curiosityreporting.data.Photo
import nl.rvbsoftdev.curiosityreporting.data.PhotoRepository.Companion.getRepository

class FavoritesViewModel(app: Application) : AndroidViewModel(app) {

    private val photoRepository = getRepository(app)

    /** Get all favorite photos from Room database through repository (uses suspend function)**/
//    init {
//        viewModelScope.launch {
//            photoRepository.getAllPhotosFromDatabase()
//        }
//    }

    val favoritePhotos: LiveData<List<Photo>> = photoRepository.favoritePhotos.asLiveData()

    private val _selectedFavoritePhoto = MutableLiveData<Photo>()

    val selectedPhoto: LiveData<Photo>
        get() = _selectedFavoritePhoto

    fun removeAllPhotoFromFavorites() {
        viewModelScope.launch {
            photoRepository.deleteAllPhotosFromDatabase()
        }
    }
}



