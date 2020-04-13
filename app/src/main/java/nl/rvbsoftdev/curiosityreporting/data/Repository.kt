package nl.rvbsoftdev.curiosityreporting.data

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.preference.PreferenceManager
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import nl.rvbsoftdev.curiosityreporting.R
import nl.rvbsoftdev.curiosityreporting.feature.favorite.database.FavoritePhotosDatabase
import nl.rvbsoftdev.curiosityreporting.feature.favorite.database.getDatabase
import nl.rvbsoftdev.curiosityreporting.feature.favorite.database.toFavoriteDatabasePhoto
import nl.rvbsoftdev.curiosityreporting.feature.favorite.database.toListOfPhoto
import nl.rvbsoftdev.curiosityreporting.network.NetworkService
import retrofit2.HttpException
import java.util.*

/** The PhotoRepository is the single data source for the app.
 * All the data the ViewModels provide the views (inside the Fragments destinations) comes from the repository.
 * ViewModels do not interact with the network or database directly (MVVM unidirectional design). **/

enum class NasaApiConnectionStatus { LOADING, ERROR, NODATA, DONE }

class PhotoRepository(private val app: Application) {

    companion object {
        private lateinit var sRepository: PhotoRepository

        fun getRepository(app: Application): PhotoRepository {
            synchronized(PhotoRepository::class.java) {
                if (!Companion::sRepository.isInitialized) {
                    sRepository = PhotoRepository(app)
                }
            }
            return sRepository
        }
    }

    private val favoritePhotosDatabase: FavoritePhotosDatabase = getDatabase(app)

    private val _connectionStatus = MutableLiveData<NasaApiConnectionStatus>()
    val connectionStatus: LiveData<NasaApiConnectionStatus> = _connectionStatus

    private val _photosFromNasaApi = MutableLiveData<List<Photo>>()
    val photosFromNasaApi: LiveData<List<Photo>> = _photosFromNasaApi

    var favoritePhotos: LiveData<List<Photo>> = favoritePhotosDatabase.favoritePhotoDao.getAllPhotos().map { it.toListOfPhoto() }

    private val _mostRecentEarthPhotoDate = MutableLiveData<String>()
    val mostRecentEarthPhotoDate: LiveData<String> = _mostRecentEarthPhotoDate

    private val _mostRecentSolPhotoDate = MutableLiveData<Int>()
    val mostRecentSolPhotoDate: LiveData<Int> = _mostRecentSolPhotoDate

    /** if present apply personal NASA API key **/
    private val apiKey = when (PreferenceManager.getDefaultSharedPreferences(app).getString("nasa_key", null).isNullOrEmpty()) {
        true -> app.resources.getString(R.string.nasa_api_key)
        else -> PreferenceManager.getDefaultSharedPreferences(app).getString("nasa_key", null)!!
    }

    /** All network and database operations run on the Kotlin Coroutine Dispatcher IO to prevent blocking the UI/Main Thread **/
    suspend fun getPhotos(earthDate: String? = null, sol: Int? = null, camera: String? = null) {
        withContext(IO) {
            _connectionStatus.postValue(NasaApiConnectionStatus.LOADING)

            try {
                // Get the list of NetworkPhoto's from the NASA API and convert it to a list of Photo's
                val result: List<Photo> = NetworkService.NETWORK_SERVICE.getNasaJsonResponse(earthDate, sol, camera, apiKey = apiKey).toListOfPhoto()
                // Get a list of favorite photo id's
                val favoritePhotosIds: List<Int> = favoritePhotosDatabase.favoritePhotoDao.getAllPhotos().value?.toListOfPhoto()?.map { it.id } ?: emptyList()
                // Mark every Photo that matches an id in favoritePhotosIds as a favorite
                result.forEach {
                    if (favoritePhotosIds.contains(it.id)) it.isFavorite = true
                }
                _photosFromNasaApi.postValue(result)
                when {
                    result.isEmpty() -> _connectionStatus.postValue(NasaApiConnectionStatus.NODATA)
                    else -> _connectionStatus.postValue(NasaApiConnectionStatus.DONE)
                }
            } catch (e: HttpException) {
                _connectionStatus.postValue(NasaApiConnectionStatus.ERROR)
                Log.e("nasa api error", e.toString())
            }
        }
    }

    suspend fun getMostRecentDates() {
        withContext(IO) {

            try {
                val getMostRecentDates = NetworkService.NETWORK_SERVICE.getNasaJsonResponse("2019-05-01", null, null, apiKey = apiKey).toListOfPhoto()
                _mostRecentEarthPhotoDate.postValue(getMostRecentDates[0].rover.max_date)
                _mostRecentSolPhotoDate.postValue(getMostRecentDates[0].rover.max_sol)
            } catch (e: Exception) {

                val calender = Calendar.getInstance()
                val currentYear = calender.get(Calendar.YEAR)
                val currentMonth = calender.get(Calendar.MONTH)
                val currentDay = calender.get(Calendar.DAY_OF_MONTH)

                val monthConverted = currentMonth + 1
                val currentDate = "${currentYear}-${monthConverted}-${currentDay}"
                _mostRecentEarthPhotoDate.postValue(currentDate)
            }
        }
    }

    suspend fun getAllPhotosFromDatabase() {
        withContext(IO) {
            favoritePhotos = favoritePhotosDatabase.favoritePhotoDao.getAllPhotos().map { it.toListOfPhoto() }
        }
    }

    suspend fun addPhotoToDatabase(photo: Photo) = withContext(IO) { favoritePhotosDatabase.favoritePhotoDao.insert(photo.toFavoriteDatabasePhoto()) }

    suspend fun removePhotoFromDatabase(photo: Photo) = withContext(IO) { favoritePhotosDatabase.favoritePhotoDao.delete(photo.toFavoriteDatabasePhoto()) }

    suspend fun deleteAllPhotosFromDatabase() = withContext(IO) { favoritePhotosDatabase.favoritePhotoDao.deleteAllPhotos() }
}

