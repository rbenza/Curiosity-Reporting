package nl.rvbsoftdev.curiosityreporting.data

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import nl.rvbsoftdev.curiosityreporting.R
import nl.rvbsoftdev.curiosityreporting.feature.favorite.database.FavoritePhotosDatabase
import nl.rvbsoftdev.curiosityreporting.feature.favorite.database.getDatabase
import nl.rvbsoftdev.curiosityreporting.feature.favorite.database.toFavoriteDatabasePhoto
import nl.rvbsoftdev.curiosityreporting.feature.favorite.database.toListOfPhoto
import retrofit2.HttpException

/** The PhotoRepository is the single data source for the app.
 * All the data the ViewModels provide the views (inside the Fragments destinations) comes from the repository.
 * ViewModels do not interact with the network or database directly (MVVM unidirectional design). **/

enum class NasaApiConnectionStatus { LOADING, ERROR, NO_DATA, DONE }

class Repository(app: Application) {

    companion object {
        private lateinit var sRepository: Repository

        fun getRepository(app: Application): Repository {
            synchronized(Repository::class.java) {
                if (!Companion::sRepository.isInitialized) {
                    sRepository = Repository(app)
                }
            }
            return sRepository
        }
    }

    private val favoritePhotosDatabase: FavoritePhotosDatabase = getDatabase(app)

    private val _connectionStatus = MutableLiveData<NasaApiConnectionStatus>()
    val connectionStatus: LiveData<NasaApiConnectionStatus>
        get() = _connectionStatus

    private val _photosFromNasaApi = MutableLiveData<List<Photo>>()
    val photosFromNasaApi: MutableLiveData<List<Photo>>
        get() = _photosFromNasaApi

    val favoritePhotos: Flow<List<Photo>>
        get() = favoritePhotosDatabase.favoritePhotoDao.getAllPhotos().map { it.toListOfPhoto() }

    private val _mostRecentEarthPhotoDate = MutableLiveData<String?>()
    val mostRecentEarthPhotoDate: LiveData<String?>
        get() = _mostRecentEarthPhotoDate

    private val _mostRecentSolPhotoDate = MutableLiveData<Int>()
    val mostRecentSolPhotoDate: LiveData<Int>
        get() = _mostRecentSolPhotoDate

    /** if present apply personal NASA API key **/
    private val apiKey = when (PreferenceManager.getDefaultSharedPreferences(app).getString("nasa_key", null).isNullOrEmpty()) {
        true -> app.resources.getString(R.string.nasa_api_key)
        else -> PreferenceManager.getDefaultSharedPreferences(app).getString("nasa_key", null)!!
    }

    /** All network and database operations run on the Kotlin Coroutine Dispatcher IO to prevent blocking the UI/Main Thread **/
    suspend fun getPhotosWithSolOrEathDate(earthDate: String? = null, sol: Int? = null, camera: String? = null) {
        withContext(IO) {
            _connectionStatus.postValue(NasaApiConnectionStatus.LOADING)
            try {
                // Get the list of NetworkPhoto's from the NASA API and convert it to a list of Photo's
                val result: List<Photo>? = NetworkService.NETWORK_SERVICE.getPhotosWithSolOrEarthDate(earthDate, sol, camera, apiKey = apiKey).toListOfPhoto()

//                // Get a list of id's of the favorite photos
//                val favoritePhotosIds: List<Int> = favoritePhotos.toList().flatMap {
//                    it.map { photo ->
//                        photo.id
//                    }
//                }
//                // Mark every Photo that matches an id in favoritePhotosIds as a favorite
//                result.forEach { photo ->
//                    if (favoritePhotosIds.contains(photo.id)) photo.isFavorite = true
//                }
                _photosFromNasaApi.postValue(result)
                when {
                    result?.isEmpty()!! -> _connectionStatus.postValue(NasaApiConnectionStatus.NO_DATA)
                    else -> _connectionStatus.postValue(NasaApiConnectionStatus.DONE)
                }
            } catch (e: HttpException) {
                _connectionStatus.postValue(NasaApiConnectionStatus.ERROR)
                Log.e("nasa api error", e.toString())
            }
        }
    }

    suspend fun getLatestPhotos() {
        withContext(IO) {
            _connectionStatus.postValue(NasaApiConnectionStatus.LOADING)
            try {
                // Get the list of NetworkPhoto's from the NASA API and convert it to a list of Photo's
                val result: List<Photo>? = NetworkService.NETWORK_SERVICE.getLatestPhotos(apiKey).toListOfPhoto()

//                // Get a list of id's of the favorite photos
//                val favoritePhotosIds: List<Int> = favoritePhotos.toList().flatMap {
//                    it.map { photo ->
//                        photo.id
//                    }
//                }
//                // Mark every Photo that matches an id in favoritePhotosIds as a favorite
//                result.forEach { photo ->
//                    if (favoritePhotosIds.contains(photo.id)) photo.isFavorite = true
//                }
                _photosFromNasaApi.postValue(result)
                when {
                    result?.isEmpty()!! -> _connectionStatus.postValue(NasaApiConnectionStatus.NO_DATA)
                    else -> _connectionStatus.postValue(NasaApiConnectionStatus.DONE)
                }
            } catch (e: HttpException) {
                _connectionStatus.postValue(NasaApiConnectionStatus.ERROR)
                Log.e("nasa api error", e.toString())
            }
        }
    }



    suspend fun getMostRecentDates() {
        val getMostRecentDates = NetworkService.NETWORK_SERVICE.getLatestPhotos(apiKey).toListOfPhoto()
        getMostRecentDates?.firstOrNull()?.let {
            _mostRecentEarthPhotoDate.postValue(it.earth_date)
            _mostRecentSolPhotoDate.postValue(it.sol)
        }
    }

    suspend fun addPhotoToDatabase(photo: Photo) = favoritePhotosDatabase.favoritePhotoDao.insert(photo.toFavoriteDatabasePhoto())

    suspend fun removePhotoFromDatabase(photo: Photo) = favoritePhotosDatabase.favoritePhotoDao.delete(photo.toFavoriteDatabasePhoto())

    suspend fun deleteAllPhotosFromDatabase() = favoritePhotosDatabase.favoritePhotoDao.deleteAllPhotos()
}

