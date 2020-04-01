import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.Assert.assertEquals
import nl.rvbsoftdev.curiosityreporting.favorite.database.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/** Test to verify the Room database by inserting a mocked photo and searching for it.
 * Includes LiveData extension property to enable Room to return it's value synchronously instead of asynchronously.
 * See reply from Yigit Boyar (Android Technical Lead @ Google) https://stackoverflow.com/questions/44270688/unit-testing-room-and-livedata **/

@RunWith(AndroidJUnit4::class)
class RoomDatabaseTest {
    private lateinit var favoritePhotoDao: FavoritePhotoDao
    private lateinit var db: FavoritePhotosDatabase

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
                context, FavoritePhotosDatabase::class.java).allowMainThreadQueries().build()
        favoritePhotoDao = db.favoritePhotoDao
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndFindPhotoInDatabase() {
        val mockedFavoriteDatabasePhoto = FavoriteDatabasePhoto(12345,
                DatabaseCamera("the mast", "mast"),
                "2019-01-1",
                "wwww.fakeurl.com",
                2000, DatabaseRover("2019-08-08", 3000, 360000))
        favoritePhotoDao.insertFavoritePhoto(mockedFavoriteDatabasePhoto)
        assertEquals(mockedFavoriteDatabasePhoto, favoritePhotoDao.find(12345).blockingValue)
    }


    private val <T> LiveData<T>.blockingValue : T?
        get() {
            var value: T? = null
            val latch = CountDownLatch(1)
            observeForever {
                value = it
                latch.countDown()
            }
            if (latch.await(2, TimeUnit.SECONDS)) return value
            else throw Exception("LiveData value was not set within 2 seconds")
        }
}