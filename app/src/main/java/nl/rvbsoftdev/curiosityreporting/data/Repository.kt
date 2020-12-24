package nl.rvbsoftdev.curiosityreporting.data

import android.app.Application
import androidx.preference.PreferenceManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import nl.rvbsoftdev.curiosityreporting.R
import nl.rvbsoftdev.curiosityreporting.feature.favorite.database.FavoriteDatabasePhoto
import nl.rvbsoftdev.curiosityreporting.feature.favorite.database.FavoritePhotosDatabase
import nl.rvbsoftdev.curiosityreporting.feature.favorite.database.getDatabase
import nl.rvbsoftdev.curiosityreporting.feature.favorite.database.toFavoriteDatabasePhoto
import nl.rvbsoftdev.curiosityreporting.feature.favorite.database.toListOfPhoto
import retrofit2.HttpException
import timber.log.Timber

/** The PhotoRepository is the single data source for the app.
 * All the data the ViewModels provide the views (inside the Fragments destinations) comes from the repository.
 * ViewModels do not interact with the network or database directly (MVVM unidirectional design). **/

enum class NasaApiConnectionStatus { IDLE, LOADING, ERROR, NO_DATA }

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

    val favoritePhotos: Flow<List<Photo>> = favoritePhotosDatabase.favoritePhotoDao.observePhotos().map { it.toListOfPhoto() }

    private val _connectionStatus = MutableStateFlow(NasaApiConnectionStatus.IDLE)
    val connectionStatus: StateFlow<NasaApiConnectionStatus> get() = _connectionStatus

    private val _mostRecentEarthPhotoDate = MutableStateFlow<String?>(null)
    val mostRecentEarthPhotoDate: StateFlow<String?> get() = _mostRecentEarthPhotoDate

    private val _mostRecentSolPhotoDate = MutableStateFlow<Int?>(null)
    val mostRecentSolPhotoDate: StateFlow<Int?> get() = _mostRecentSolPhotoDate

    /** if present apply personal NASA API key **/
    private val apiKey = when (PreferenceManager.getDefaultSharedPreferences(app).getString("nasa_key", null).isNullOrEmpty()) {
        true -> app.resources.getString(R.string.nasa_api_key)
        else -> PreferenceManager.getDefaultSharedPreferences(app).getString("nasa_key", null)!!
    }

    /** Using suspend functions for one shot asynchronous calls **/
    suspend fun getPhotosWithSolOrEathDate(earthDate: String? = null, sol: Int? = null, camera: String? = null): List<Photo>? {
        return try {
            // Set connectionStatus to loading
            _connectionStatus.value = NasaApiConnectionStatus.LOADING

            // Get the list of NetworkPhoto's from the NASA API and convert it to a list of Photo's
            val result = NetworkService.RETRO_FIT.getPhotosWithSolOrEarthDate(earthDate, sol, camera, apiKey = apiKey)?.toListOfPhoto()

            // Get a list of id's of the favorite photos
            val favoritePhotosIds: List<Int?> = favoritePhotosDatabase.favoritePhotoDao.getAllPhotos().map { it.id }

            // Mark every Photo that matches an id in favoritePhotosIds as a favorite
            result?.forEach { photo ->
                if (favoritePhotosIds.contains(photo.id)) photo.isFavorite = true
            }

            _connectionStatus.value = if (result?.isEmpty()!!) NasaApiConnectionStatus.NO_DATA else NasaApiConnectionStatus.IDLE

            result

        } catch (e: HttpException) {
            _connectionStatus.value = NasaApiConnectionStatus.ERROR
            Timber.e(e)
            null
        }
    }

    suspend fun getLatestPhotos(): List<Photo>? {
        return try {
            // Set connectionStatus to loading
            _connectionStatus.value = NasaApiConnectionStatus.LOADING

            // Get the list of the lastest NetworkPhoto's from the NASA API and convert it to a list of Photo's
            val result = NetworkService.RETRO_FIT.getLatestPhotos(apiKey)?.toListOfPhoto()

            // Get a list of id's of the favorite photos
            val favoritePhotosIds: List<Int?> = favoritePhotosDatabase.favoritePhotoDao.getAllPhotos().map { it.id }

            // Mark every Photo that matches an id in favoritePhotosIds as a favorite
            result?.forEach { photo ->
                if (favoritePhotosIds.contains(photo.id)) photo.isFavorite = true
            }
            _connectionStatus.value = if (result?.isEmpty()!!) NasaApiConnectionStatus.NO_DATA else NasaApiConnectionStatus.IDLE

            result
        } catch (e: HttpException) {
            _connectionStatus.value = NasaApiConnectionStatus.ERROR
            Timber.e(e)
            null
        }
    }

    suspend fun getMostRecentDates() {
        val getMostRecentDates = NetworkService.RETRO_FIT.getLatestPhotos(apiKey)?.toListOfPhoto()
        getMostRecentDates?.firstOrNull()?.let {
            _mostRecentEarthPhotoDate.value = it.earth_date
            _mostRecentSolPhotoDate.value = it.sol ?: -1
        }
    }

    suspend fun addPhotoToDatabase(photo: Photo) = favoritePhotosDatabase.favoritePhotoDao.insert(photo.toFavoriteDatabasePhoto())

    suspend fun removePhotoFromDatabase(photo: Photo) = favoritePhotosDatabase.favoritePhotoDao.delete(photo.toFavoriteDatabasePhoto())

    suspend fun deleteAllPhotosFromDatabase() = favoritePhotosDatabase.favoritePhotoDao.deleteAllPhotos()
}

