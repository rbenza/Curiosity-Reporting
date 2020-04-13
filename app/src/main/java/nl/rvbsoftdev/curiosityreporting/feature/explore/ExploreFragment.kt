package nl.rvbsoftdev.curiosityreporting.feature.explore

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.MenuCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.GridLayoutManager
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
        viewModel.photosFromNasaApi.observe(viewLifecycleOwner, Observer { listOfNetworkPhotos ->
                       binding.recyclerviewPhotosExplore.adapter = ExplorePhotoAdapter(viewLifecycleOwner, ExplorePhotoAdapter.OnClickListener { photo ->
                findNavController().navigate(ExploreFragmentDirections.actionExploreFragmentToExploreDetailFragment(photo))
            }).apply { submitList(listOfNetworkPhotos) }
        })

        /** Lets the user select a list or grid as preference **/
        var gridOrList = 4
        when (PreferenceManager.getDefaultSharedPreferences(requireContext()).getString("explore_photo_layout", "Grid")) {
            "Grid" -> gridOrList = 4
            "List" -> gridOrList = 1
        }
        binding.recyclerviewPhotosExplore.layoutManager = GridLayoutManager(requireContext(), gridOrList)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_explore_menu, menu)
        MenuCompat.setGroupDividerEnabled(menu, true)
        super.onCreateOptionsMenu(menu, inflater)
    }

    /**Camera filter options menu**/
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.all_cameras -> {
                viewModel.setCameraFilter(null)
                showCameraFilterSnackBar("all cameras")
                item.isChecked = true
            }
            R.id.FHAZ -> {
                viewModel.setCameraFilter("FHAZ")
                showCameraFilterSnackBar("the Front Hazard Avoidance Camera")
                item.isChecked = true
            }
            R.id.RHAZ -> {
                viewModel.setCameraFilter("RHAZ")
                showCameraFilterSnackBar("the Rear Hazard Avoidance Camera")
                item.isChecked = true
            }
            R.id.MAST -> {
                viewModel.setCameraFilter("MAST")
                showCameraFilterSnackBar("the Mast Camera")
                item.isChecked = true
            }
            R.id.CHEMCAM -> {
                viewModel.setCameraFilter("CHEMCAM")
                showCameraFilterSnackBar("the Chemistry and Camera Complex")
                item.isChecked = true
            }
            R.id.MAHLI -> {
                viewModel.setCameraFilter("MAHLI")
                showCameraFilterSnackBar("the Mars Hand Lens Imager")
                item.isChecked = true
            }
            R.id.MARDI -> {
                viewModel.setCameraFilter("MARDI")
                showCameraFilterSnackBar("the Mars Descent Imager")
                item.isChecked = true
            }
            R.id.NAVCAM -> {
                viewModel.setCameraFilter("NAVCAM")
                showCameraFilterSnackBar("the Navigation Camera")
                item.isChecked = true
            }
            R.id.launch_date_picker_dialog -> showDatePickerDialog()

            R.id.select_random_date -> {
                val mostRecentSol = viewModel.mostRecentSolPhotoDate.value
                var randomBound = 2540
                if (mostRecentSol != null) {
                    randomBound = mostRecentSol
                }
                val randomSol = Random().nextInt(randomBound)
                viewModel.refreshPhotos(null, randomSol, null)
                navigationActivity.showStyledSnackbarMessage(requireView(),
                        text = "Roll the dice!\nSelected Mars solar day $randomSol!",
                        durationMs = 3000,
                        icon = R.drawable.icon_dice)
            }
        }
        return true
    }

   private fun showCameraFilterSnackBar(camera: String) {
        navigationActivity.showStyledSnackbarMessage(requireView(),
                text = "Camera filter for $camera selected",
                durationMs = 2500,
                icon = R.drawable.icon_camera)
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
        viewModel.refreshPhotos(userSelectedDate, null, null)
    }
}