package nl.rvbsoftdev.curiosityreporting.feature.favorite

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.view.MenuCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.GridLayoutManager
import nl.rvbsoftdev.curiosityreporting.R
import nl.rvbsoftdev.curiosityreporting.databinding.FragmentFavoritesBinding
import nl.rvbsoftdev.curiosityreporting.global.BaseFragment
import nl.rvbsoftdev.curiosityreporting.global.NavigationActivity

/** Favorites Fragment that provides a unique List of Photos sorted by most recent earth date contained in the local room database **/

class FavoritesFragment : BaseFragment<FragmentFavoritesBinding>() {

    override val layout = R.layout.fragment_favorites
    override val firebaseTag = "Favorite Fragment"
    private val viewModel: FavoritesViewModel by lazy { ViewModelProviders.of(this).get(FavoritesViewModel::class.java)  }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.favoritesViewModel = viewModel
        binding.recyclerviewPhotoFavorites.adapter = FavoritesPhotoAdapter(FavoritesPhotoAdapter.OnClickListener {
            viewModel.displayFavoritePhotoDetails(it)
        })

        /** Lets the user select a list or grid as preference **/
        var listOrGrid = 1
        if (PreferenceManager.getDefaultSharedPreferences(requireContext()).getString("favorites_photo_layout", "List") == "Grid") {
            listOrGrid = 3
        }
        binding.recyclerviewPhotoFavorites.layoutManager = GridLayoutManager(requireContext(), listOrGrid)
        viewModel.navigateToSelectedPhoto.observe(this, Observer {
            if (it != null) {
                this.findNavController().navigate(FavoritesFragmentDirections.actionFavoritesFragmentToFavoritesDetailFragment(it))
                viewModel.displayFavoritePhotoDetailsFinished()
            }
        })
        /** Disables 'delete all' menu option when no photos present**/
        viewModel.favoritePhotos.observe(this, Observer {
            if (it.isNullOrEmpty()) setHasOptionsMenu(false) else setHasOptionsMenu(true)
        })

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
                                viewModel.removeAllPhotoFromFavorites()
                                (it as NavigationActivity).showStyledSnackbarMessage(requireView(),
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

