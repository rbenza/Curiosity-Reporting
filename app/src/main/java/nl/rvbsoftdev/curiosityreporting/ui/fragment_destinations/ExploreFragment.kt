package nl.rvbsoftdev.curiosityreporting.ui.fragment_destinations

import android.os.Bundle
import android.view.*
import androidx.core.view.MenuCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.analytics.FirebaseAnalytics
import nl.rvbsoftdev.curiosityreporting.R
import nl.rvbsoftdev.curiosityreporting.viewmodels.SharedViewModel
import nl.rvbsoftdev.curiosityreporting.databinding.FragmentExploreBinding
import java.util.*
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import nl.rvbsoftdev.curiosityreporting.adapters.ExplorePhotoAdapter
import nl.rvbsoftdev.curiosityreporting.ui.single_activity.SingleActivity
import nl.rvbsoftdev.curiosityreporting.viewmodels.ExploreViewModel

/** Explore Fragment where the user can view the photos from the NASA database through a random Sol generator or DatePicker dialog **/

class ExploreFragment : Fragment(), DatePickerDialog.OnDateSetListener {

    private val mViewModel: ExploreViewModel by lazy {
        ViewModelProviders.of(this).get(ExploreViewModel::class.java)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "Explore Fragment")
        (activity as SingleActivity).firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
        val dataBinding = FragmentExploreBinding.inflate(inflater)
        dataBinding.lifecycleOwner = this
        dataBinding.exploreViewModel = mViewModel
        dataBinding.recyclerviewPhotosExplore.adapter =
                ExplorePhotoAdapter(ExplorePhotoAdapter.OnClickListener {
            mViewModel.displayPhotoDetails(it)
        })
        /** Lets the user select a list or grid as preference **/
        var gridOrList = 4
        if (PreferenceManager.getDefaultSharedPreferences(requireContext()).
                        getString("explore_photo_layout", "Grid") == "List") {
            gridOrList = 1
        }
        dataBinding.recyclerviewPhotosExplore.layoutManager = GridLayoutManager(requireContext(), gridOrList)
        mViewModel.navigateToSelectedPhoto.observe(this, Observer {
            if (it != null) {
                this.findNavController().
                        navigate(ExploreFragmentDirections.actionExploreFragmentToExploreDetailFragment(it))
                mViewModel.displayPhotoDetailsFinished()
            }
        })
        mViewModel.cameraFilterStatus.observe(this, Observer {
            if(it == "Error") {
                (activity as SingleActivity).
                        showStyledToastMessage("Please reselect the date, this small issue will be fixed soon.") }
        })

        setHasOptionsMenu(true)
        return dataBinding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_explore_menu, menu)
        MenuCompat.setGroupDividerEnabled(menu, true)
        super.onCreateOptionsMenu(menu, inflater)
    }

    /**Camera filter options menu**/

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        fun showCameraFilterSnackBar(camera: String) {
            (activity as SingleActivity).showStyledSnackbarMessage(requireView(),
                    "Camera filter for $camera selected",
                    null, 2500, R.drawable.icon_camera, null)
        }

        when (item.itemId) {
            R.id.all_cameras -> {
                mViewModel.setCameraFilter(null)
                showCameraFilterSnackBar("all cameras")
                item.isChecked = true
            }

            R.id.FHAZ -> {
                mViewModel.setCameraFilter("FHAZ")
                item.isChecked = true
                showCameraFilterSnackBar("the Front Hazard Avoidance Camera")
            }
            R.id.RHAZ -> {
                mViewModel.setCameraFilter("RHAZ")
                item.isChecked = true
                showCameraFilterSnackBar("the Rear Hazard Avoidance Camera")
            }
            R.id.MAST -> {
                mViewModel.setCameraFilter("MAST")
                showCameraFilterSnackBar("the Mast Camera")
                item.isChecked = true
            }
            R.id.CHEMCAM -> {
                mViewModel.setCameraFilter("CHEMCAM")
                showCameraFilterSnackBar("the Chemistry and Camera Complex")
                item.isChecked = true
            }
            R.id.MAHLI -> {
                mViewModel.setCameraFilter("MAHLI")
                showCameraFilterSnackBar("the Mars Hand Lens Imager")
                item.isChecked = true
            }
            R.id.MARDI -> {
                mViewModel.setCameraFilter("MARDI")
                showCameraFilterSnackBar("the Mars Descent Imager")
                item.isChecked = true
            }
            R.id.NAVCAM -> {
                mViewModel.setCameraFilter("NAVCAM")
                showCameraFilterSnackBar("the Navigation Camera")
                item.isChecked = true
            }
            R.id.launch_date_picker_dialog -> showDatePickerDialog()

            R.id.select_random_date -> {
                val mostRecentSol = mViewModel.mostRecentSolPhotoDate.value
                var randomBound = 2540
                if (mostRecentSol != null) {
                    randomBound = mostRecentSol
                }
                val randomSol = Random().nextInt(randomBound)
                mViewModel.refreshPhotos(null, randomSol, null)
                (activity as SingleActivity).showStyledSnackbarMessage(requireView(),
                        "Roll the dice!\nSelected Mars solar day $randomSol!",
                        null, 3000, R.drawable.icon_dice, null)
            }
        }
        return true
    }

    /** Custom Datepicker fragment where user can select a photo date.
     * Tries to fetch te most recent date from the NASA API. When unsuccessful uses date of today **/

    private fun showDatePickerDialog() {

        try {
            val mostRecentPhotoDate = mViewModel.mostRecentEarthPhotoDate.value
            val mostRecentYear = Integer.valueOf(mostRecentPhotoDate!!.slice(0..3))
            val mostRecentMonth = (Integer.valueOf(mostRecentPhotoDate!!.slice(5..6))) - 1
            val mostRecentDay = Integer.valueOf(mostRecentPhotoDate!!.slice(8..9))

            val dpd = DatePickerDialog.newInstance(this, mostRecentYear, mostRecentMonth, mostRecentDay)
            dpd.setTitle("Curiosity most recent Photos are taken on " +
                    SharedViewModel.DateFormatter.formatDate(mostRecentPhotoDate))
            if ((activity as SingleActivity).setAndReturnUserTheme() == "Dark") dpd.isThemeDark = true
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
            if ((activity as SingleActivity).setAndReturnUserTheme() == "Dark") dpd.isThemeDark = true
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
        (activity as SingleActivity).showStyledSnackbarMessage(requireView(),
                "Date selected: " + SharedViewModel.DateFormatter.formatDate(userSelectedDate),
                null, 3500, R.drawable.icon_calender, null)
        mViewModel.refreshPhotos(userSelectedDate, null, null)
    }
}





