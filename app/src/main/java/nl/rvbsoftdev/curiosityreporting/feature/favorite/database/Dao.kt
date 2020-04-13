package nl.rvbsoftdev.curiosityreporting.feature.favorite.database

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.Dao
import kotlinx.coroutines.flow.Flow

@Dao
interface Dao {

    @Query("SELECT * FROM favoritedatabasephoto ORDER BY earth_date DESC")
    fun getAllPhotos(): LiveData<List<FavoriteDatabasePhoto>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(favoriteDatabasePhoto: FavoriteDatabasePhoto)

    @Delete
    fun delete(favoriteDatabasePhoto: FavoriteDatabasePhoto)

    @Query("DELETE FROM favoritedatabasephoto")
    fun deleteAllPhotos()
}
