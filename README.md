<img src="https://user-images.githubusercontent.com/47558082/63434368-3a275100-c425-11e9-9844-c76070631088.jpg"></img> 

### *Explore Mars through the eyes of the NASA Mars Rover 'Curiosity'*

Curiosity Reporting is an Android app developed for educational purposes and a fascination for Mars.

This app let's you:

* View the most recent pictures Curiosity transmits back to Earth every week
* Explore years of amazing pictures Curiosity has taken
* Save and share your favorite pictures. 
* Get to know more about the mission of Curiosity and Mars

All the photos are retrieved from the [public database](https://api.nasa.gov/api.html#MarsPhotos) the NASA provides. Available on the Google Play Store.

[<img src="https://user-images.githubusercontent.com/47558082/63432359-4e694f00-c421-11e9-99c1-81eccc3d19d3.png" width="40%"></img>](https://play.google.com/store/apps/details?id=nl.rvbsoftdev.curiosityreporting)

## App screenshots

<img src="https://user-images.githubusercontent.com/47558082/65385007-879b2480-dd29-11e9-9c9a-5c3cd59c3b7d.jpg" width="23%"></img> <img src="https://user-images.githubusercontent.com/47558082/65384985-4c98f100-dd29-11e9-88bc-0c0f3d12a3aa.jpg" width="23%"></img> <img src="https://user-images.githubusercontent.com/47558082/65385008-879b2480-dd29-11e9-942f-90358d9f7fc7.jpg" width="23%"></img> <img src="https://user-images.githubusercontent.com/47558082/65385005-87028e00-dd29-11e9-805b-e58ddf6e9bdb.jpg" width="23%"></img> <img src="https://user-images.githubusercontent.com/47558082/65385006-879b2480-dd29-11e9-906e-425765dada42.jpg" width="23%"></img> <img src="https://user-images.githubusercontent.com/47558082/65384983-4c98f100-dd29-11e9-8f6a-8c179b913160.jpg" width="23%"></img> <img src="https://user-images.githubusercontent.com/47558082/65384984-4c98f100-dd29-11e9-8bd7-620a6415eed9.jpg" width="23%"></img> 


## App architecture 

Curiosity Reporting is built with best practices and recommended architecture in accordance with Google's [guide to Android app architecture](https://developer.android.com/jetpack/docs/guide). It incorporates a Model-View-ViewModel architecture as displayed on the image below. It uses a single activity design patern with multiple fragments as destinations.

![final-architecture](https://user-images.githubusercontent.com/47558082/63265861-d8cd7980-c28e-11e9-8e75-6cfacedfb22a.png)

## Android Jetpack

[Android Jetpack](https://developer.android.com/jetpack) is a set of components, tools and guidance to enable developers to write high-quality apps. Jetpack is built around modern design practices like separation of concerns and testability. Curiosity Reporting adapts most of the Jetpack libraries and the latest features Kotlin provides (Android KTX, Coroutines).

![jetpack](https://user-images.githubusercontent.com/47558082/63265845-d10dd500-c28e-11e9-8ead-b99463f5a4b1.png)

## App components

* **UI/UX:** Dark/Light themes, custom styled toast/snackbar messages, custom DatePicker dialog, custom ImageView, launch screen, weekly notifications, grid or listviews
* **Layouts:** RecyclerView with DiffUtil, global Toolbar for all fragments, ConstraintLayout, CoordinatorLayout, CollapsingToolbar Layout, DrawerLayout
* **Navigation:** Jetpack Navigation with SafeArgs, bottom navigation bar, menu navigation
* **Data:** Jetpack LiveData, Jetpack databinding, custom binding adapters 
* **Persistence:** Jetpack Room SQLite database, Jetpack Preferences, Jetpack ViewModels
* **Network:** Retrofit, Glide and Moshi to connect and parse JSON response from NASA REST API
* **Threading:** Kotlin Coroutines for background tasks (Dispatchers IO/Main)
* **Background:** JetPack WorkManager to perform background work while app is not running
* **Testing/Performance:** Espresso, JUnit, Android Studio Profiler, Firebase TestLab, Analytics and Crashlytics
* **Tools:** Android Studio, Git(hub) and Photoshop

## Contact developer

Bug reports or feature requests can be made. Other questions or feedback can be send to the developer at: rvbsoftdev@gmail.com. 
