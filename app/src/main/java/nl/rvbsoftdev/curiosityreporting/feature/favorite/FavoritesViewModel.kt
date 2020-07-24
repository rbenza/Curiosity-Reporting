package nl.rvbsoftdev.curiosityreporting.feature.favorite

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import nl.rvbsoftdev.curiosityreporting.data.Photo
import nl.rvbsoftdev.curiosityreporting.data.Repository
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import java.util.*

class FavoritesViewModel(app: Application) : AndroidViewModel(app) {

    private val photoRepository = Repository.getRepository(app)

    /** Get all favorite photos from Room database through repository (uses suspend function)**/

    val favoritePhotos: LiveData<List<Photo>> = photoRepository.favoritePhotos

    val selectedFavoritePhoto = MutableLiveData<Photo>()

    fun removeAllPhotoFromFavorites() {
        viewModelScope.launch {
            photoRepository.deleteAllPhotosFromDatabase()
        }
    }

    fun removePhotoFromFavorites(photo: Photo) {
        viewModelScope.launch {
            photoRepository.removePhotoFromDatabase(photo)
        }
    }

    fun formatStringDate(input: String?): String {
        if (input == null) return ""
        val toLocalDate = LocalDate.parse(input, DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault()))
        return DateTimeFormatter.ofPattern("d MMM yyyy").format(toLocalDate)
    }
}



