package nl.rvbsoftdev.curiosityreporting.explore

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.MenuCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.GridLayoutManager
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import nl.rvbsoftdev.curiosityreporting.R
import nl.rvbsoftdev.curiosityreporting.databinding.FragmentExploreBinding
import nl.rvbsoftdev.curiosityreporting.explore.ExploreFragmentDirections
import nl.rvbsoftdev.curiosityreporting.global.BaseFragment
import nl.rvbsoftdev.curiosityreporting.global.NavigationActivity
import nl.rvbsoftdev.curiosityreporting.global.SharedViewModel
import java.util.*

/** Explore Fragment where the user can view the photos from the NASA database through a random Sol generator or DatePicker dialog **/

class ExploreFragment : BaseFragment<FragmentExploreBinding>(), DatePickerDialog.OnDateSetListener {

    override val layout = R.layout.fragment_explore
    override val firebaseTag = "Explore Fragment"
    private val viewModel: ExploreViewModel by lazy { ViewModelProviders.of(this).get(ExploreViewModel::class.java) }
    private val singleActivity by lazy { activity as NavigationActivity }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.exploreViewModel = viewModel
        binding.recyclerviewPhotosExplore.adapter =
                ExplorePhotoAdapter(ExplorePhotoAdapter.OnClickListener {
                    viewModel.displayPhotoDetails(it)
                })
        /** Lets the user select a list or grid as preference **/
        var gridOrList = 4
        if (PreferenceManager.getDefaultSharedPreferences(requireContext()).
                        getString("explore_photo_layout", "Grid") == "List") {
            gridOrList = 1
        }
        binding.recyclerviewPhotosExplore.layoutManager = GridLayoutManager(requireContext(), gridOrList)
        viewModel.navigateToSelectedPhoto.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                this.findNavController().navigate(ExploreFragmentDirections.actionExploreFragmentToExploreDetailFragment(it))
                viewModel.displayPhotoDetailsFinished()
            }
        })
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_explore_menu, menu)
        MenuCompat.setGroupDividerEnabled(menu, true)
        super.onCreateOptionsMenu(menu, inflater)
    }

    /**Camera filter options menu**/
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        fun showCameraFilterSnackBar(camera: String) {
            singleActivity.showStyledSnackbarMessage(requireView(),
                    text = "Camera filter for $camera selected",
                    durationMs = 2500,
                    icon = R.drawable.icon_camera)
        }

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
                singleActivity.showStyledSnackbarMessage(requireView(),
                        text ="Roll the dice!\nSelected Mars solar day $randomSol!",
                        durationMs = 3000,
                        icon = R.drawable.icon_dice)
            }
        }
        return true
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
                    SharedViewModel.DateFormatter.formatDate(mostRecentPhotoDate))
            if (singleActivity.setAndReturnUserTheme() == "Dark") dpd.isThemeDark = true
            dpd.showYearPickerFirst(true)
            dpd.setCancelText("Dismiss")
            dpd.minDate = SharedViewModel.CalenderObjectProvider.provideCalender("2012-08-07")
            dpd.maxDate = SharedViewModel.CalenderObjectProvider.provideCalender(mostRecentPhotoDate)
            dpd.show(fragmentManager!!, "Datepickerdialog")

        } catch (e: Exception) {
            val calender = Calendar.getInstance()
            val currentYear = calender.get(Calendar.YEAR)
            val currentMonth = calender.get(Calendar.MONTH)
            val currentDay = calender.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog.newInstance(this, currentYear, currentMonth, currentDay)
            if (singleActivity.setAndReturnUserTheme() == "Dark") dpd.isThemeDark = true
            dpd.setTitle(getString(R.string.most_recent_date_not_available))
            dpd.showYearPickerFirst(true)
            dpd.setCancelText("Dismiss")
            dpd.minDate = SharedViewModel.CalenderObjectProvider.provideCalender("2012-08-07")
            dpd.show(fragmentManager!!, "Datepickerdialog")
        }
    }

    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        val monthConverted = monthOfYear + 1
        val userSelectedDate = "${year}-${monthConverted}-${dayOfMonth}"
        singleActivity.showStyledSnackbarMessage(requireView(),
                text = "Date selected: " + SharedViewModel.DateFormatter.formatDate(userSelectedDate),
                durationMs = 3500,
                icon = R.drawable.icon_calender)
        viewModel.refreshPhotos(userSelectedDate, null, null)
    }
}