package nl.rvbsoftdev.curiosityreporting.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import nl.rvbsoftdev.curiosityreporting.domain.Photo

class FavoritesDetailViewModelFactory(
        val photo: Photo,
        val application: Application) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoritesDetailViewModel::class.java)) {
            return FavoritesDetailViewModel(photo, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
