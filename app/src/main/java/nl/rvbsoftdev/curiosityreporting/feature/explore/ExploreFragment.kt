package nl.rvbsoftdev.curiosityreporting.feature.explore

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.MenuCompat
import androidx.core.view.forEach
import androidx.fragment.app.activityViewModels
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.CompositeDateValidator
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.stfalcon.imageviewer.StfalconImageViewer
import nl.rvbsoftdev.curiosityreporting.R
import nl.rvbsoftdev.curiosityreporting.data.Photo
import nl.rvbsoftdev.curiosityreporting.databinding.FragmentExploreBinding
import nl.rvbsoftdev.curiosityreporting.global.BaseFragment
import nl.rvbsoftdev.curiosityreporting.global.NavigationActivity
import nl.rvbsoftdev.curiosityreporting.global.PhotoOverlay
import nl.rvbsoftdev.curiosityreporting.global.SharedViewModel
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZoneOffset
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter.ofPattern
import timber.log.Timber
import java.util.*

/** Explore Fragment where the user can view the photos from the NASA database through a random Sol generator or MaterialDatePicker dialog **/

class ExploreFragment : BaseFragment<FragmentExploreBinding>() {

    override val layout = R.layout.fragment_explore
    override val firebaseTag = "Explore Fragment"
    private val viewModel: ExploreViewModel by activityViewModels() // used for caching, normally tied to Fragment lifecycle
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val navigationActivity by lazy { activity as NavigationActivity }
    private lateinit var builder: StfalconImageViewer<Photo>
    private lateinit var photoOverlay: PhotoOverlay
    private lateinit var explorePhotoAdapter: ExplorePhotoAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.exploreViewModel = viewModel

        explorePhotoAdapter = ExplorePhotoAdapter(viewLifecycleOwner, viewModel) { photo, position ->
            viewModel.setSelectedPhoto(photo)
            photoOverlay = PhotoOverlay(requireContext()).apply {
                setupClickListenersAndVm(viewModel, { builder.close() }, { sharePhoto() }, clickFavorite = { viewModel.toggleFavorite(photo) })
            }
            if (viewModel.filteredPhotos.value != null) {
                setupSwipeImageViewer(viewModel.filteredPhotos.value!!, position)
            } else {
                viewModel.photos.value?.let {
                    setupSwipeImageViewer(it, position)
                }
            }
        }

        /** Set up observer for the photo's loaded from the NASA API **/
        viewModel.photos.observe(viewLifecycleOwner) { list ->
            binding.recyclerviewPhotosExplore.adapter = explorePhotoAdapter.apply {
                submitList(list)
            }
        }
        /** Uses a shared viewmodel tied to NavigationActivity lifecycle for FavoriteFragment to communicate with ExploreFragment **/
        sharedViewModel.deletedAllFavorites.observe(viewLifecycleOwner) { deletedAll ->
            if (deletedAll) {
                viewModel.deleteAllFavorites()
                sharedViewModel.deletedAllFavorites.value = false
            }
        }
        sharedViewModel.deletedFavoritePhoto.observe(viewLifecycleOwner) { viewModel.removedPhotoFromFavorites(it) }
        viewModel.iconConnectionStatus.observe(viewLifecycleOwner) { binding.statusImage.setImageDrawable(it) }

        /** Lets the user select a list or grid as preference **/
        val gridOrList = when (PreferenceManager.getDefaultSharedPreferences(requireContext()).getString("explore_photo_layout", "Grid")) {
            "Grid" -> 4
            else -> 1
        }

        binding.recyclerviewPhotosExplore.layoutManager = GridLayoutManager(requireContext(), gridOrList)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_explore_menu, menu)
        val menuItemsToAlwaysShow = listOf(R.id.select_random_date, R.id.launch_date_picker_dialog, R.id.camera_filter_title, R.id.all_cameras)

        /** Dynamically hide the camera filters for photos not in the list **/
        viewModel.photos.observe(viewLifecycleOwner) { list ->
            menu.forEach { menuItem ->
                menuItem.isVisible = list?.any { it.camera?.full_name == menuItem.title } == true || menuItemsToAlwaysShow.contains(menuItem.itemId)
            }
        }

        MenuCompat.setGroupDividerEnabled(menu, true)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun setupSwipeImageViewer(list: List<Photo>, position: Int) {
        builder = StfalconImageViewer.Builder(requireContext(), list) { view, img ->
            Glide.with(requireContext()).load(img.img_src).into(view)
        }
                .withImageChangeListener {
                    viewModel.setSelectedPhoto(viewModel.photos.value?.get(it))
                    photoOverlay.setInfoText(getString(
                            R.string.explore_detail_photo_taken_on,
                            viewModel.selectedPhoto.value?.camera?.full_name,
                            viewModel.formatStringDate(viewModel.selectedPhoto.value?.earth_date ?: ""),
                            viewModel.selectedPhoto.value?.sol))
                }
                .withStartPosition(position)
                .withOverlayView(photoOverlay)
                .show()
    }

    /** Camera filter options menu **/
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.all_cameras -> {
                setCameraFilter(null, null, item)
                explorePhotoAdapter.apply { submitList(viewModel.photos.value) }
                viewModel.resetPhotoFilter()
            }
            R.id.FHAZ -> setCameraFilter("FHAZ", "the Front Hazard Avoidance Camera", item)
            R.id.RHAZ -> setCameraFilter("RHAZ", "the Rear Hazard Avoidance Camera", item)
            R.id.MAST -> setCameraFilter("MAST", "the Mast Camera", item)
            R.id.CHEMCAM -> setCameraFilter("CHEMCAM", "the Chemistry and Camera Complex", item)
            R.id.MAHLI -> setCameraFilter("MAHLI", "the Mars Hand Lens Imager", item)
            R.id.MARDI -> setCameraFilter("MARDI", "the Mars Descent Imager", item)
            R.id.NAVCAM -> setCameraFilter("NAVCAM", "the Navigation Camera", item)
            R.id.launch_date_picker_dialog -> launchDatePicker()

            R.id.select_random_date -> {
                val mostRecentSol = viewModel.mostRecentSolPhotoDate
                var randomBound = 2979
                if (mostRecentSol != null) {
                    randomBound = mostRecentSol
                }
                val randomSol = Random().nextInt(randomBound)
                viewModel.getPhotos(null, randomSol, null)
                navigationActivity.showStyledSnackbarMessage(requireView(),
                        text = "Roll the dice!\nSelected Mars solar day $randomSol!",
                        durationMs = 3000,
                        icon = R.drawable.icon_dice)
            }
        }
        return true
    }

    private fun setCameraFilter(cameraFilter: String?, cameraSnackbar: String?, menuItem: MenuItem) {
        viewModel.setCameraFilter(cameraFilter)
        explorePhotoAdapter.apply { submitList(viewModel.filteredPhotos.value) }
        val msg = if (cameraFilter == null) "Showing all ${viewModel.photos.value?.size} photos" else "Filtered ${viewModel.filteredPhotos.value?.size} photos from the $cameraSnackbar"
        navigationActivity.showStyledSnackbarMessage(requireView(), msg, 2500, R.drawable.icon_camera)
        menuItem.isChecked = true
    }

    /** Uses Glide to convert the selected photo to a bitmap and share. Invokes requestPermission function to request runtime permission for storage access **/
    private fun sharePhoto() {
        if (requireContext().checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CODE)
        } else {
            try {
                if (!viewModel.selectedPhoto.value?.img_src.isNullOrEmpty()) {
                    Glide.with(requireContext())
                            .asBitmap()
                            .load(viewModel.selectedPhoto.value?.img_src)
                            .into(object : CustomTarget<Bitmap>() {
                                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                    val imagePath = MediaStore.Images.Media.insertImage(navigationActivity.contentResolver,
                                            resource, "Curiosity Mars Image ${viewModel.selectedPhoto.value?.earth_date}", null)
                                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "image/*"
                                        putExtra(Intent.EXTRA_TEXT, getString(R.string.sharing_photo_message, viewModel.formatStringDate(viewModel.selectedPhoto.value?.earth_date)))
                                        putExtra(Intent.EXTRA_STREAM, Uri.parse(imagePath))
                                    }

                                    if (shareIntent.resolveActivity(navigationActivity.packageManager) != null) {
                                        startActivity(shareIntent)
                                    } else {
                                        navigationActivity.showStyledToastMessage(getString(R.string.no_app_to_share_photo))
                                    }
                                }

                                override fun onLoadCleared(placeholder: Drawable?) {
                                }
                            })
                }
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    /** Material DatePicker where user can select a photo date. **/
    private fun launchDatePicker() {
        viewModel.getMostRecentDates()
        val mostRecentPhotoDateAsString = viewModel.mostRecentEarthPhotoDate
        val mostRecentPhotoDateAsLocalDate = if (mostRecentPhotoDateAsString != null) {
            LocalDateTime.parse(mostRecentPhotoDateAsString.plus("-14:00"), ofPattern("yyyy-MM-dd-HH:mm")).toEpochSecond(ZoneOffset.UTC) * 1000
        } else {
            ZonedDateTime.now().toEpochSecond() * 1000 // use today as limit if most recent photo date not available from NASA API
        }
        val curiosityLandingDateOnMars = LocalDateTime.parse("2012-08-07T14:00:00").toEpochSecond(ZoneOffset.UTC) * 1000

        val selectionRange = ArrayList<CalendarConstraints.DateValidator>().apply {
            add(DateValidatorPointForward.from(curiosityLandingDateOnMars))
            add(DateValidatorPointBackward.before(mostRecentPhotoDateAsLocalDate))
        }

        val calendarConstraints = CalendarConstraints.Builder()
                .setValidator(CompositeDateValidator.allOf(selectionRange))
                .setStart(curiosityLandingDateOnMars) // day Curiosity landed on Mars
                .setOpenAt(mostRecentPhotoDateAsLocalDate)
                .setEnd(mostRecentPhotoDateAsLocalDate)
                .build()

        val title = if (!mostRecentPhotoDateAsString.isNullOrBlank()) getString(R.string.most_recent_photo_date, viewModel.formatStringDate(mostRecentPhotoDateAsString)) else getString(R.string.select_a_date)

        val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(title)
                .setCalendarConstraints(calendarConstraints)
                .setSelection(mostRecentPhotoDateAsLocalDate)
                .setTheme(R.style.CR_DatePicker)
                .build()

        datePicker.addOnPositiveButtonClickListener { dateSelected: Long ->
            val apiDate = ZonedDateTime.ofInstant(Instant.ofEpochMilli(dateSelected), ZoneId.systemDefault()).format(ofPattern(("yyyy-MM-dd")))
            val msgDate = ZonedDateTime.ofInstant(Instant.ofEpochMilli(dateSelected), ZoneId.systemDefault()).format(ofPattern(("d MMMM yyyy")))
            navigationActivity.showStyledSnackbarMessage(
                    requireView(),
                    text = "Date selected: $msgDate",
                    durationMs = 3500,
                    icon = R.drawable.icon_calender)
            viewModel.getPhotos(apiDate, null, null)
        }
        datePicker.show(requireActivity().supportFragmentManager, "cr date picker")
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CODE && (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            sharePhoto()
        } else {
            navigationActivity.showStyledToastMessage(getString(R.string.storage_access_required))
        }
    }

    companion object {
        const val REQUEST_CODE = 1
    }
}