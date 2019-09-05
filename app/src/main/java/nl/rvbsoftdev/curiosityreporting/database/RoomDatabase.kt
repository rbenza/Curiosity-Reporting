package nl.rvbsoftdev.curiosityreporting.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

/** Local Room database to store favorite photos selected by the user in the 'Explore' Fragment **/

@Dao
interface FavoritePhotoDao {

    @Query("SELECT * FROM favoritedatabasephoto ORDER BY earth_date DESC")
    fun getFavoritePhotos(): LiveData<List<FavoriteDatabasePhoto>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertFavoritePhoto(favoriteDatabasePhoto: FavoriteDatabasePhoto)

    @Delete
    fun deleteFavorite(favoriteDatabasePhoto: FavoriteDatabasePhoto)

    @Query("DELETE FROM favoritedatabasephoto")
    fun deleteAllFavorites()

    @Query("SELECT * FROM favoritedatabasephoto WHERE id LIKE :search")
    fun find(search: Int): LiveData<FavoriteDatabasePhoto>
}

@Database(entities = [FavoriteDatabasePhoto::class], version = 8, exportSchema = false)
abstract class FavoritePhotosDatabase : RoomDatabase() {
    abstract val favoritePhotoDao: FavoritePhotoDao
}
    /** Singleton synchronized Room database**/
    private lateinit var sDATABASE: FavoritePhotosDatabase

    fun getDatabase(context: Context): FavoritePhotosDatabase {
        synchronized(FavoritePhotosDatabase::class.java) {
            if (!::sDATABASE.isInitialized) {
                sDATABASE = Room
                        .databaseBuilder(context.applicationContext,
                        FavoritePhotosDatabase::class.java,
                        "favoritephotosdatabase")
                        .fallbackToDestructiveMigration()
                        .build()
            }
        }
        return sDATABASE
    }



