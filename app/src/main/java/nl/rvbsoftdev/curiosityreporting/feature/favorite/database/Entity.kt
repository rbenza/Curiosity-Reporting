package nl.rvbsoftdev.curiosityreporting.feature.favorite.database

import androidx.room.*
import nl.rvbsoftdev.curiosityreporting.data.Photo

/** Room database table with only unique items (id property is unique) and ordered by earth_date **/

@SuppressWarnings(RoomWarnings.PRIMARY_KEY_FROM_EMBEDDED_IS_DROPPED)

@Entity(indices = [Index(value = ["id"], unique = true)])
data class FavoriteDatabasePhoto (
        @PrimaryKey
        val id: Int,
        @Embedded val camera: DatabaseCamera,
        val earth_date: String,
        val img_src: String,
        val sol: Int,
        @Embedded val rover: DatabaseRover)

@Entity
data class DatabaseRover (
        @PrimaryKey(autoGenerate = true)
        val max_date: String,
        val max_sol: Int,
        val total_photos: Int)

@Entity
data class DatabaseCamera (
        @PrimaryKey(autoGenerate = true)
        val full_name: String,
        val name: String)

/** Various Kotlin Extension Functions to map a (List of) 'FavoriteDatabasePhoto' to a (List of) 'Photo' **/

fun List<FavoriteDatabasePhoto>.toListOfPhoto(): List<Photo> {
    return map { favoriteDatabasePhoto ->
        Photo(
                id = favoriteDatabasePhoto.id,
                camera = favoriteDatabasePhoto.camera.toCamera(),
                earth_date = favoriteDatabasePhoto.earth_date,
                img_src = favoriteDatabasePhoto.img_src,
                sol = favoriteDatabasePhoto.sol,
                rover = favoriteDatabasePhoto.rover.toRover())
    }
}

fun Photo.toFavoriteDatabasePhoto(): FavoriteDatabasePhoto {
    return let {
        FavoriteDatabasePhoto(
                id = it.id,
                camera = it.camera.toFavoriteDatabasePhoto(),
                earth_date = it.earth_date,
                img_src = it.img_src,
                sol = it.sol,
                rover = it.rover.toFavoriteDatabasePhoto())
    }
}

fun FavoriteDatabasePhoto.toPhoto(): Photo {
    return let {
        Photo(
                id = it.id,
                camera = it.camera.toCamera(),
                earth_date = it.earth_date,
                img_src = it.img_src,
                sol = it.sol,
                rover = it.rover.toRover())
    }
}

fun Photo.Camera.toFavoriteDatabasePhoto(): DatabaseCamera {
    return let {
        DatabaseCamera(full_name = it.full_name, name = it.name)
    }
}


fun Photo.Rover.toFavoriteDatabasePhoto(): DatabaseRover {
    return let {
        DatabaseRover(
                max_date = it.max_date,
                max_sol = it.max_sol,
                total_photos = it.total_photos)
    }
}

fun DatabaseCamera.toCamera(): Photo.Camera {
    return let {
        Photo.Camera(full_name = it.full_name, name = it.name)
    }
}

fun DatabaseRover.toRover(): Photo.Rover {
    return let {
        Photo.Rover(max_date = it.max_date, max_sol = it.max_sol, total_photos = it.total_photos)
    }
}


