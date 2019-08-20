<img src="https://user-images.githubusercontent.com/47558082/63291622-33cd9380-c2c4-11e9-8219-da5d841eabd8.png" width="40%"></img>

### *Explore Mars through the eyes of the NASA Mars Rover 'Curiosity'*

Curiosity Reporting is an Android app developed for educational purposes and a fascination for Mars.

This app let's you:

* View the most recent pictures Curiosity transmits back to Earth every week
* Explore years of amazing pictures Curiosity has taken
* Save and share your favorite pictures. 
* Get to know more about the mission of Curiosity and Mars

All the photos are retrieved from the [public database](https://api.nasa.gov/api.html#MarsPhotos) the NASA provides. Available on the [Google Play Store](https://play.google.com/store/apps/details?id=nl.rvbsoftdev.curiosityreporting).

## App screenshots

<img src="https://user-images.githubusercontent.com/47558082/63264743-1bda1d80-c28c-11e9-86ad-e086507572ac.jpg" width="23%"></img> <img src="https://user-images.githubusercontent.com/47558082/63264744-1bda1d80-c28c-11e9-834c-c495e5ba6331.jpg" width="23%"></img> <img src="https://user-images.githubusercontent.com/47558082/63264746-1bda1d80-c28c-11e9-98a5-697f5b4df033.jpg" width="23%"></img> <img src="https://user-images.githubusercontent.com/47558082/63264747-1c72b400-c28c-11e9-85b5-381692eb0718.jpg" width="23%"></img> <img src="https://user-images.githubusercontent.com/47558082/63264748-1c72b400-c28c-11e9-9183-b024fc0a4ea3.jpg" width="23%"></img> <img src="https://user-images.githubusercontent.com/47558082/63264749-1c72b400-c28c-11e9-84a8-78fecef1b735.jpg" width="23%"></img> <img src="https://user-images.githubusercontent.com/47558082/63264751-1c72b400-c28c-11e9-938d-215bdace9668.jpg" width="23%"></img> 


## App architecture 

Curiosity Reporting is built with best practices and recommended architecture in accordance with Google's [guide to Android app architecture](https://developer.android.com/jetpack/docs/guide). It incorporates a Model-View-ViewModel architecture as displayed on the image below. It uses a single activity design patern with multiple fragments as destinations.

![final-architecture](https://user-images.githubusercontent.com/47558082/63265861-d8cd7980-c28e-11e9-8e75-6cfacedfb22a.png)

## Android Jetpack

[Android Jetpack](https://developer.android.com/jetpack) is a set of components, tools and guidance to enable developers to write high-quality apps. Jetpack is built around modern design practices like separation of concerns and testability. Curiosity Reporting adapts most of the Jetpack libraries and the latest features Kotlin provides (Android KTX, Coroutines).

![jetpack](https://user-images.githubusercontent.com/47558082/63265845-d10dd500-c28e-11e9-8ead-b99463f5a4b1.png)

## App components

* **UI/UX:** Dark/Light themes, custom styled toast/snackbar messages, custom DatePicker dialog, custom ImageView, launch screen
* **Layouts:** RecyclerView with DiffUtil, global Toolbar for all fragments, ConstraintLayout, CoordinatorLayout, CollapsingToolbar Layout, DrawerLayout
* **Navigation:** Jetpack Navigation with SafeArgs, bottom navigation bar, menu navigation
* **Data:** Jetpack LiveData, Jetpack databinding, custom binding adapters 
* **Persistence:** Jetpack Room SQLite database, Jetpack Preferences, Jetpack ViewModels
* **Network:** Retrofit, Glide and Moshi to connect and parse JSON response from NASA REST API
* **Threading:** Kotlin Coroutines for background tasks (Dispatchers IO/Main)
* **Testing/Performance:** Espresso, JUnit, Firebase TestLab, Firebase Analytics, Firebase Crashlytics
* **Tools:** Android Studio, Github and Photoshop

## Contact developer

Bug reports or feature requests can be made. Other questions or feedback can be send to the developer at: rvbsoftdev@gmail.com. 
