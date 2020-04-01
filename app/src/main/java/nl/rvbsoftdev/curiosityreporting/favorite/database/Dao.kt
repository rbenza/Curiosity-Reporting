package nl.rvbsoftdev.curiosityreporting.favorite.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface Dao {

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
