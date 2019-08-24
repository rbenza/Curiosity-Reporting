package nl.rvbsoftdev.curiosityreporting.background_work

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import nl.rvbsoftdev.curiosityreporting.repository.PhotoRepository
import retrofit2.HttpException
import java.util.*

/** Worker class to perform work when app is not running. Uses CoroutineWorker to run suspend function **/

class RefreshPhotos(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    companion object {
        const val WORK_NAME = "getNewestPhotos"
    }

    override suspend fun doWork(): Result {
        Log.i("do work","fetching photos in background")
        val photoRepository = PhotoRepository.getRepository(Application())

        return try {
            photoRepository.getMostRecentDates()
            if (!photoRepository.mostRecentEarthPhotoDate.value.isNullOrEmpty()) {
                photoRepository.getPhotos(photoRepository.mostRecentEarthPhotoDate.value)
                Result.success()
            } else {
                photoRepository.getPhotos(null, Random().nextInt(2491))
                Result.success()
            }
        } catch (e: HttpException) {
            Result.retry()
        }
    }
}
