package nl.rvbsoftdev.curiosityreporting.feature.favorite.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/** Local Room database to store favorite photos selected by the user in the 'Explore' Fragment **/

@Database(entities = [FavoriteDatabasePhoto::class], version = 8, exportSchema = false)
abstract class FavoritePhotosDatabase : RoomDatabase() {
    abstract val favoritePhotoDao: Dao
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



