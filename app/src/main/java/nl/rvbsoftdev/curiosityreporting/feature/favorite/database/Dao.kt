package nl.rvbsoftdev.curiosityreporting.feature.favorite.database

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.Dao

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
