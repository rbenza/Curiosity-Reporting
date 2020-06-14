package nl.rvbsoftdev.curiosityreporting.feature.explore

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.MenuCompat
import androidx.core.view.forEach
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import nl.rvbsoftdev.curiosityreporting.R
import nl.rvbsoftdev.curiosityreporting.databinding.FragmentExploreBinding
import nl.rvbsoftdev.curiosityreporting.global.BaseFragment
import nl.rvbsoftdev.curiosityreporting.global.NavigationActivity
import nl.rvbsoftdev.curiosityreporting.global.formatDate
import nl.rvbsoftdev.curiosityreporting.global.provideCalender
import java.util.*

/** Explore Fragment where the user can view the photos from the NASA database through a random Sol generator or DatePicker dialog **/

class ExploreFragment : BaseFragment<FragmentExploreBinding>(), DatePickerDialog.OnDateSetListener {

    override val layout = R.layout.fragment_explore
    override val firebaseTag = "Explore Fragment"
    private val viewModel: ExploreViewModel by viewModels()
    private val navigationActivity by lazy { activity as NavigationActivity }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.exploreViewModel = viewModel

        /** Set up observer for the photo's loaded from the NASA API **/
        viewModel.photosFromNasaApi.observe(viewLifecycleOwner, Observer { list ->
            binding.recyclerviewPhotosExplore.adapter = ExplorePhotoAdapter(viewLifecycleOwner, { photo ->
                findNavController().navigate(ExploreFragmentDirections.actionExploreFragmentToExploreDetailFragment(photo))
            }).apply {
                stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.ALLOW
                submitList(list)
            }
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
            R.id.launch_date_picker_dialog -> showDatePickerDialog()

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

    /** Custom Datepicker fragment where user can select a photo date.
     * Tries to fetch te most recent date from the NASA API. When unsuccessful uses date of today **/

    private fun showDatePickerDialog() {

        try {
            val mostRecentPhotoDate = viewModel.mostRecentEarthPhotoDate.value
            val mostRecentYear = Integer.valueOf(mostRecentPhotoDate!!.slice(0..3))
            val mostRecentMonth = (Integer.valueOf(mostRecentPhotoDate.slice(5..6))) - 1
            val mostRecentDay = Integer.valueOf(mostRecentPhotoDate.slice(8..9))

            val dpd = DatePickerDialog.newInstance(this, mostRecentYear, mostRecentMonth, mostRecentDay)
            dpd.setTitle("Curiosity most recent Photos are taken on " +
                    formatDate(mostRecentPhotoDate))
            if (navigationActivity.setAndReturnUserTheme() == "Dark") dpd.isThemeDark = true
            dpd.showYearPickerFirst(true)
            dpd.setCancelText("Dismiss")
            dpd.minDate = provideCalender("2012-08-07")
            dpd.maxDate = provideCalender(mostRecentPhotoDate)
            dpd.show(activity?.supportFragmentManager!!, "Datepickerdialog")

        } catch (e: Exception) {
            val calender = Calendar.getInstance()
            val currentYear = calender.get(Calendar.YEAR)
            val currentMonth = calender.get(Calendar.MONTH)
            val currentDay = calender.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog.newInstance(this, currentYear, currentMonth, currentDay)
            if (navigationActivity.setAndReturnUserTheme() == "Dark") dpd.isThemeDark = true
            dpd.setTitle(getString(R.string.most_recent_date_not_available))
            dpd.showYearPickerFirst(true)
            dpd.setCancelText("Dismiss")
            dpd.minDate = provideCalender("2012-08-07")
            dpd.show(activity?.supportFragmentManager!!, "Datepickerdialog")
        }
    }

    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        val monthConverted = monthOfYear + 1
        val userSelectedDate = "${year}-${monthConverted}-${dayOfMonth}"
        navigationActivity.showStyledSnackbarMessage(requireView(),
                text = "Date selected: " + formatDate(userSelectedDate),
                durationMs = 3500,
                icon = R.drawable.icon_calender)
        viewModel.getPhotos(userSelectedDate, null, null)
    }
}