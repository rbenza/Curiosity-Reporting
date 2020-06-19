package nl.rvbsoftdev.curiosityreporting.feature.explore

import android.content.Intent
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
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.datepicker.*
import com.stfalcon.imageviewer.StfalconImageViewer
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import nl.rvbsoftdev.curiosityreporting.R
import nl.rvbsoftdev.curiosityreporting.data.Photo
import nl.rvbsoftdev.curiosityreporting.databinding.FragmentExploreBinding
import nl.rvbsoftdev.curiosityreporting.global.*
import org.threeten.bp.*
import org.threeten.bp.format.DateTimeFormatter.*
import java.time.format.DateTimeFormatter
import java.util.*

/** Explore Fragment where the user can view the photos from the NASA database through a random Sol generator or DatePicker dialog **/

class ExploreFragment : BaseFragment<FragmentExploreBinding>() {

    override val layout = R.layout.fragment_explore
    override val firebaseTag = "Explore Fragment"
    private val viewModel: ExploreViewModel by viewModels()
    private val navigationActivity by lazy { activity as NavigationActivity }
    private lateinit var builder: StfalconImageViewer<Photo>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.exploreViewModel = viewModel

        val explorePhotoAdapter = ExplorePhotoAdapter(viewLifecycleOwner) { photo, position ->
            viewModel.selectedPhoto.value = photo
            val photoOverlay = PhotoOverlay(requireContext()).apply { setupClickListenersAndVm(viewModel, { builder.close() }, { sharePhoto() }, { viewModel.toggleFavorite(photo) }) }
            viewModel.photosFromNasaApi.value?.let { setupSwipeImageViewer(it, position, photoOverlay) }
        }

        /** Set up observer for the photo's loaded from the NASA API **/
        viewModel.photosFromNasaApi.observe(viewLifecycleOwner, Observer { list ->
            binding.recyclerviewPhotosExplore.adapter = explorePhotoAdapter.apply { submitList(list) }
        })

        /** Lets the user select a list or grid as preference **/
        var gridOrList = 4
        when (PreferenceManager.getDefaultSharedPreferences(requireContext()).getString("explore_photo_layout", "Grid")) {
            "Grid" -> gridOrList = 4
            "List" -> gridOrList = 1
        }
        binding.recyclerviewPhotosExplore.layoutManager = GridLayoutManager(requireContext(), gridOrList)
        setHasOptionsMenu(true)

        viewModel.iconConnectionStatus.observe(viewLifecycleOwner, Observer {
            binding.statusImage.setImageDrawable(it)
        })

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_explore_menu, menu)
        val menuItemsToAlwaysShow = listOf(R.id.select_random_date, R.id.launch_date_picker_dialog, R.id.camera_filter_title, R.id.all_cameras)

//        /** Dynamically hide the camera filters for photos not in the list **/
//        viewModel.photosFromNasaApi.observe(viewLifecycleOwner, Observer { list ->
//            list.any { photo ->
//                menu.forEach { menuItem ->
//                    menuItem.isVisible = photo.camera.full_name == menuItem.title || menuItemsToAlwaysShow.all { it == menuItem.itemId }
//                }
//                true
//            }
//        })

        MenuCompat.setGroupDividerEnabled(menu, true)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun setupSwipeImageViewer(list: List<Photo>, position: Int, photoOverlay: PhotoOverlay? = null) {
        builder = StfalconImageViewer.Builder(requireContext(), list) { view, img ->
            Glide.with(requireContext()).load(img.img_src).into(view)
        }
                .withImageChangeListener {
                    viewModel.selectedPhoto.value = viewModel.photosFromNasaApi.value?.get(it)
                }
                .withStartPosition(position)
                .withOverlayView(photoOverlay)
                .show()
    }


    /** Camera filter options menu **/
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.all_cameras -> setCameraFilter(null, "all cameras", item)
            R.id.FHAZ -> setCameraFilter("FHAZ", "the Front Hazard Avoidance Camera", item)
            R.id.RHAZ -> setCameraFilter("RHAZ", "the Rear Hazard Avoidance Camera", item)
            R.id.MAST -> setCameraFilter("MAST", "the Mast Camera", item)
            R.id.CHEMCAM -> setCameraFilter("CHEMCAM", "the Chemistry and Camera Complex", item)
            R.id.MAHLI -> setCameraFilter("MAHLI", "the Mars Hand Lens Imager", item)
            R.id.MARDI -> setCameraFilter("MARDI", "the Mars Descent Imager", item)
            R.id.NAVCAM -> setCameraFilter("NAVCAM", "the Navigation Camera", item)
            R.id.launch_date_picker_dialog -> launchDateRangePicker()

            R.id.select_random_date -> {
                val mostRecentSol = viewModel.mostRecentSolPhotoDate.value
                var randomBound = 2540
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

    private fun setCameraFilter(cameraFilter: String?, cameraSnackbar: String, menuItem: MenuItem) {
        viewModel.setCameraFilter(cameraFilter)
        navigationActivity.showStyledSnackbarMessage(requireView(), "Camera filter for $cameraSnackbar selected", 2500, R.drawable.icon_camera)
        menuItem.isChecked = true
    }

    private fun sharePhoto() {
        try {
            if (!viewModel.selectedPhoto.value?.img_src.isNullOrEmpty()) {
                Glide.with(requireContext())
                        .asBitmap()
                        .load(viewModel.selectedPhoto.value?.img_src)
                        .into(object : CustomTarget<Bitmap>() {
                            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                try {
                                    val imagePath = MediaStore.Images.Media.insertImage(navigationActivity.contentResolver,
                                            resource, "Curiosity Mars Image " + viewModel.selectedPhoto.value?.earth_date, null)
                                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "image/*"
                                        putExtra(Intent.EXTRA_TEXT, "Check out this amazing photo NASA's Mars Rover Curiosity captured on ${formatDate(viewModel.selectedPhoto.value?.earth_date)}!")
                                        putExtra(Intent.EXTRA_STREAM, Uri.parse(imagePath))
                                    }

                                    if (shareIntent.resolveActivity(navigationActivity.packageManager) != null) {
                                        startActivity(shareIntent)
                                    } else {
                                        navigationActivity.showStyledToastMessage(getString(R.string.no_app_to_share_photo))
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }

                            override fun onLoadCleared(placeholder: Drawable?) {
                            }
                        })
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** Custom Datepicker fragment where user can select a photo date.
     * Tries to fetch te most recent date from the NASA API. When unsuccessful uses date of today **/

    private fun launchDateRangePicker() {
        val now = ZonedDateTime.now().toEpochSecond() * 1000
        val curiosityLandingDateOnMars = LocalDateTime.parse("2012-08-07T14:00:00").toEpochSecond(ZoneOffset.UTC) * 1000

        //
        val selectionRange = ArrayList<CalendarConstraints.DateValidator>().apply {
            add(DateValidatorPointForward.from(curiosityLandingDateOnMars))
            add(DateValidatorPointBackward.before(now))
        }

        val calendarConstraints = CalendarConstraints.Builder()
                .setValidator(CompositeDateValidator.allOf(selectionRange))
                .setStart(curiosityLandingDateOnMars) // day Curiosity landed on Mars
                .setOpenAt(now)
                .setEnd(now)
                .build()

        val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(getString(R.string.most_recent_photo_date, viewModel.mostRecentEarthPhotoDate.value ?: ""))
                .setCalendarConstraints(calendarConstraints)
                .setSelection(now)
                .setTheme(R.style.CR_DatePicker)
                .build()

        datePicker.addOnPositiveButtonClickListener { dateSelected: Long ->
            val formattedDate = ZonedDateTime.ofInstant(Instant.ofEpochMilli(dateSelected), ZoneId.systemDefault()).format(ofPattern(("yyyy-MM-dd")))
            navigationActivity.showStyledSnackbarMessage(
                    requireView(),
                    text = "Date selected: $formattedDate",
                    durationMs = 3500,
                    icon = R.drawable.icon_calender)
            viewModel.getPhotos(formattedDate, null, null)
        }

        datePicker.show(requireActivity().supportFragmentManager, "cr date picker")
    }
}