package nl.rvbsoftdev.curiosityreporting.data

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query

/** Retrofit NASA API service with Moshi JSON converter **/

private const val BASE_URL = "https://api.nasa.gov/"

interface NasaAPI {

    /** no CallBacks since using Kotlin Coroutines **/
    @GET("mars-photos/api/v1/rovers/curiosity/photos")
    suspend fun getPhotosWithSolOrEarthDate(@Query("earth_date") earthDate: String?,
                                            @Query("sol") sol: Int?,
                                            @Query("camera") camera: String?,
                                            @Query("api_key") apiKey: String): NetworkPhotoContainer


    @GET("mars-photos/api/v1/rovers/curiosity/latest_photos")
    suspend fun getLatestPhotos(@Query("api_key") apiKey: String): NetworkPhotoContainer

}


@ExperimentalSerializationApi
private val retrofit = Retrofit.Builder()
        .addConverterFactory(Json{
            isLenient = true
            ignoreUnknownKeys = true
        }.asConverterFactory("application/json".toMediaType()))
        .baseUrl(BASE_URL)
        .build()

object NetworkService {
    @ExperimentalSerializationApi
    val NETWORK_SERVICE: NasaAPI by lazy { retrofit.create(NasaAPI::class.java) }
}
