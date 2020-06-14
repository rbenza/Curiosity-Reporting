package nl.rvbsoftdev.curiosityreporting.feature.favorite.database

import androidx.room.*
import androidx.room.Dao
import kotlinx.coroutines.flow.Flow

@Dao
interface Dao {

    @Query("SELECT * FROM favoritedatabasephoto ORDER BY earth_date DESC")
    fun getAllPhotos(): Flow<List<FavoriteDatabasePhoto>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favoriteDatabasePhoto: FavoriteDatabasePhoto)

    @Delete
    suspend fun delete(favoriteDatabasePhoto: FavoriteDatabasePhoto)

    @Query("DELETE FROM favoritedatabasephoto")
    suspend fun deleteAllPhotos()
}
