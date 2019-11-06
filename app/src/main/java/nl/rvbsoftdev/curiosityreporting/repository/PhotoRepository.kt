package nl.rvbsoftdev.curiosityreporting.repository

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.preference.PreferenceManager
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import nl.rvbsoftdev.curiosityreporting.database.*
import nl.rvbsoftdev.curiosityreporting.domain.Photo
import nl.rvbsoftdev.curiosityreporting.network.NasaApi
import nl.rvbsoftdev.curiosityreporting.network.asAppDataModel
import okhttp3.Dispatcher
import java.util.*

/** The PhotoRepository is the single data source for the app.
 * All the data the ViewModels provide the Views (the Fragments destinations) comes from the repository.
 * ViewModels do not interact with the network or database directly (MVVM unidirectional design). **/

enum class NasaApiConnectionStatus { LOADING, ERROR, NODATA, DONE }

class PhotoRepository(private val app: Application) {

    companion object {
        private lateinit var sRepository: PhotoRepository

        fun getRepository(app: Application): PhotoRepository {
            synchronized(PhotoRepository::class.java) {
                if (!::sRepository.isInitialized) {
                    sRepository = PhotoRepository(app)
                }
            }
            return sRepository
        }
    }

    private val _favoritePhotosDatabase: FavoritePhotosDatabase = getDatabase(app)

    private val _connectionStatus = MutableLiveData<NasaApiConnectionStatus>()

    val connectionStatus: LiveData<NasaApiConnectionStatus>
        get() = _connectionStatus

    private val _photosResultFromNasaApi = MutableLiveData<List<Photo>>()

    val photosResultFromNasaApi: LiveData<List<Photo>>
        get() = _photosResultFromNasaApi

    val favoritePhotos: LiveData<List<Photo>> =
            Transformations.map(_favoritePhotosDatabase.favoritePhotoDao.getFavoritePhotos()) {
                it.asDataBaseModel()
            }

    private var _mostRecentEarthPhotoDate = MutableLiveData<String>()

    val mostRecentEarthPhotoDate: LiveData<String>
        get() = _mostRecentEarthPhotoDate

    private var _mostRecentSolPhotoDate = MutableLiveData<Int>()

    val mostRecentSolPhotoDate: LiveData<Int>
        get() = _mostRecentSolPhotoDate

    /** All network and database operations run on the Kotlin Coroutine Dispatcher IO to prevent blocking the UI/Main Thread **/

    suspend fun getPhotos(earthDate: String? = null, sol: Int? = null, camera: String? = null) {
        withContext(IO) {
            var apiKeySetbyUser = "HViCqNaudnl7iRBSheUO7kJLzq2Ja0tewak9xiY5"
            /** if present apply personal NASA API key **/
            try {
                if (!PreferenceManager.getDefaultSharedPreferences(app).getString("nasa_key", null).isNullOrEmpty()) {
                    apiKeySetbyUser = PreferenceManager.getDefaultSharedPreferences(app).getString("nasa_key", null)!!
                }

                val getPhotos = NasaApi.RETROFIT_SERVICE.getNasaJsonResponse(earthDate, sol, camera, apiKey = apiKeySetbyUser)
                _connectionStatus.postValue(NasaApiConnectionStatus.LOADING)
                val photosResult = getPhotos.await()
                _connectionStatus.postValue(NasaApiConnectionStatus.DONE)
                _photosResultFromNasaApi.postValue(photosResult.asAppDataModel())
                if (photosResult.asAppDataModel().isEmpty()) {
                    _connectionStatus.postValue(NasaApiConnectionStatus.NODATA)
                } else null
            } catch (e: Exception) {
                _connectionStatus.postValue(NasaApiConnectionStatus.ERROR)

            }
        }
    }

    suspend fun getMostRecentDates() {
        withContext(IO) {
            var apiKeySetbyUser = "HViCqNaudnl7iRBSheUO7kJLzq2Ja0tewak9xiY5"
            try {
                if (!PreferenceManager.getDefaultSharedPreferences(app).getString("nasa_key", null).isNullOrEmpty()) {
                    apiKeySetbyUser = PreferenceManager.getDefaultSharedPreferences(app).getString("nasa_key", null)!!
                }

                val getMostRecentDates = NasaApi.RETROFIT_SERVICE.getNasaJsonResponse("2019-05-01", null, null, apiKey = apiKeySetbyUser).await()
                val dateResults = getMostRecentDates.asAppDataModel()
                _mostRecentEarthPhotoDate.postValue(dateResults[0].rover.max_date)
                _mostRecentSolPhotoDate.postValue(dateResults[0].rover.max_sol)
            } catch (e: Exception) {

                val calender = Calendar.getInstance()
                val currentYear = calender.get(Calendar.YEAR)
                val currentMonth = calender.get(Calendar.MONTH)
                val currentDay = calender.get(Calendar.DAY_OF_MONTH)

                val monthConverted = currentMonth + 1
                val currentDate = "${currentYear}- ${monthConverted}-${currentDay}"
                _mostRecentEarthPhotoDate.postValue(currentDate)
            }
        }
    }

    suspend fun addPhotoToFavorites(photo: Photo) {
        withContext(IO) {
            _favoritePhotosDatabase.favoritePhotoDao.insertFavoritePhoto(photo.asDataBaseModel())
        }
    }

    suspend fun removePhotoFromFavorites(photo: Photo) {
        withContext(IO) {
            _favoritePhotosDatabase.favoritePhotoDao.deleteFavorite(photo.asDataBaseModel())
        }
    }

    suspend fun searchForPhotoInFavoritesDatabase(photo: Photo): LiveData<Photo>? {
        var searchResult: LiveData<Photo>? = null
        withContext(IO) {
            searchResult = Transformations.map(_favoritePhotosDatabase.favoritePhotoDao.find(photo.asDataBaseModel().id)) {
                it.asAppDataModel()
            }
        }
        return searchResult
    }


    suspend fun getAllFavoritesPhotos(): LiveData<List<Photo>>? {
        var resultConverted: LiveData<List<Photo>>? = null
        withContext(IO) {
            val result = _favoritePhotosDatabase.favoritePhotoDao.getFavoritePhotos()
            resultConverted = Transformations.map(result) { input: List<FavoriteDatabasePhoto>? ->
                input?.asDataBaseModel()
            }
        }
        return resultConverted
    }

    suspend fun deleteAllFavoritesPhotos() {
        withContext(IO) {
            _favoritePhotosDatabase.favoritePhotoDao.deleteAllFavorites()
        }
    }
}

