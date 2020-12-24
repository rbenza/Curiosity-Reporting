package nl.rvbsoftdev.curiosityreporting.background_work

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import nl.rvbsoftdev.curiosityreporting.data.Repository
import retrofit2.HttpException
import timber.log.Timber
import java.util.*

/** Worker class to perform work when app is not running. Uses CoroutineWorker to run suspend function **/

class RefreshPhotos(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    companion object {
        const val WORK_NAME = "getNewestPhotos"
    }

    override suspend fun doWork(): Result {
        Timber.tag("do work").i("fetching photos in background")
        val photoRepository = Repository.getRepository(applicationContext as Application)

        return try {
            photoRepository.getMostRecentDates()
            if (!photoRepository.mostRecentEarthPhotoDate.isNullOrEmpty()) {
                photoRepository.getPhotosWithSolOrEathDate(photoRepository.mostRecentEarthPhotoDate)
                Result.success()
            } else {
                photoRepository.getPhotosWithSolOrEathDate(null, Random().nextInt(2491))
                Result.success()
            }
        } catch (e: HttpException) {
            Result.retry()
        }
    }
}
