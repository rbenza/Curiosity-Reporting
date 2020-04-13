package nl.rvbsoftdev.curiosityreporting.feature.favorite

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.view.MenuCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
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
    private val viewModel: FavoritesViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.favoritesViewModel = viewModel

        viewModel.favoritePhotos.observe(viewLifecycleOwner, Observer { listOfPhotos ->
            binding.recyclerviewPhotoFavorites.adapter = FavoritePhotoAdapter(viewLifecycleOwner, FavoritePhotoAdapter.OnClickListener { photo ->
                findNavController().navigate(FavoritesFragmentDirections.actionFavoritesFragmentToFavoritesDetailFragment(photo))
            }).apply { submitList(listOfPhotos) }
             setHasOptionsMenu(!listOfPhotos.isNullOrEmpty())
        })

        /** Lets the user select a list or grid as preference **/
        var listOrGrid = 1
        if (PreferenceManager.getDefaultSharedPreferences(requireContext()).getString("favorites_photo_layout", "List") == "Grid") {
            listOrGrid = 3
        }
        binding.recyclerviewPhotoFavorites.layoutManager = GridLayoutManager(requireContext(), listOrGrid)

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
                    AlertDialog.Builder(it).apply {
                        setView(android.R.layout.select_dialog_item)
                        setTitle("Delete all photos from favorites?")
                        setPositiveButton("OK") { _, _ ->
                            viewModel.removeAllPhotoFromFavorites()
                            (it as NavigationActivity).showStyledSnackbarMessage(requireView(),
                                    text = "Favorite photo(s) deleted!",
                                    durationMs = 3000,
                                    icon = R.drawable.icon_delete_all)
                        }
                        setNegativeButton("Cancel") { _, _ -> }
                        show()
                    }
                }
            }
        }
        return true
    }
}

