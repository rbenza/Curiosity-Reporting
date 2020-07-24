package nl.rvbsoftdev.curiosityreporting.feature.favorite.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface Dao {

    @Query("SELECT * FROM favoritedatabasephoto ORDER BY earth_date DESC")
    fun getAllPhotos(): LiveData<List<FavoriteDatabasePhoto>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favoriteDatabasePhoto: FavoriteDatabasePhoto)

    @Delete
    suspend fun delete(favoriteDatabasePhoto: FavoriteDatabasePhoto)

    @Query("DELETE FROM favoritedatabasephoto")
    suspend fun deleteAllPhotos()
}
