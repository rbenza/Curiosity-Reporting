package nl.rvbsoftdev.curiosityreporting.ui.fragment_destinations

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.core.view.MenuCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.analytics.FirebaseAnalytics
import nl.rvbsoftdev.curiosityreporting.R
import nl.rvbsoftdev.curiosityreporting.adapters.FavoritesPhotoAdapter
import nl.rvbsoftdev.curiosityreporting.databinding.FragmentFavoritesBinding
import nl.rvbsoftdev.curiosityreporting.ui.single_activity.SingleActivity
import nl.rvbsoftdev.curiosityreporting.viewmodels.FavoritesViewModel

/** Favorites Fragment that provides a unique List of Photos sorted by most recent earth date contained in the local room database **/

class FavoritesFragment : Fragment() {

    private val mViewModel: FavoritesViewModel by lazy {
        ViewModelProviders.of(this).get(FavoritesViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "Favorite Fragment")
        (activity as SingleActivity).firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
        val dataBinding = FragmentFavoritesBinding.inflate(inflater)
        dataBinding.lifecycleOwner = this
        dataBinding.favoritesViewModel = mViewModel
        val adapter = FavoritesPhotoAdapter(FavoritesPhotoAdapter.OnClickListener {
            mViewModel.displayFavoritePhotoDetails(it)
        })
        dataBinding.recyclerviewPhotoFavorites.adapter = adapter

        /** Lets the user select a list or grid as preference **/
        var listOrGrid = 1
        if (PreferenceManager.getDefaultSharedPreferences(requireContext()).getString("favorites_photo_layout", "List") == "Grid") {
            listOrGrid = 3
        }
        dataBinding.recyclerviewPhotoFavorites.layoutManager = GridLayoutManager(requireContext(), listOrGrid)
        mViewModel.navigateToSelectedPhoto.observe(this, Observer {
            if (it != null) {
                this.findNavController().navigate(FavoritesFragmentDirections.actionFavoritesFragmentToFavoritesDetailFragment(it))
                mViewModel.displayFavoritePhotoDetailsFinished()
            }
        })
        /** Disables 'delete all' menu option when no photos present**/
        mViewModel.favoritePhotos.observe(this, Observer {
            if (it.isNullOrEmpty()) setHasOptionsMenu(false) else setHasOptionsMenu(true)
        })

        return dataBinding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_favorites_menu, menu)
        MenuCompat.setGroupDividerEnabled(menu, true)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.delete_all -> {
                activity?.let {
                    val builder = AlertDialog.Builder(it)
                    builder.setView(android.R.layout.select_dialog_item)
                    builder.setTitle("Delete all photos from favorites?")
                            .setPositiveButton("OK") { _, _ ->
                                mViewModel.removeAllPhotoFromFavorites()
                                (activity as SingleActivity).showStyledSnackbarMessage(requireView(),
                                        text = "Favorite photo(s) deleted!",
                                        durationMs = 3000,
                                        icon = R.drawable.icon_delete_all)
                            }
                            .setNegativeButton("Cancel") { _, _ -> }
                    builder.show()
                }
            }
        }
        return true
    }
}

