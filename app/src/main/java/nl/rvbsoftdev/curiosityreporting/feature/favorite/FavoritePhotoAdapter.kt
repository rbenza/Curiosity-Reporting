package nl.rvbsoftdev.curiosityreporting.feature.favorite

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import nl.rvbsoftdev.curiosityreporting.data.Photo
import nl.rvbsoftdev.curiosityreporting.databinding.ListItemFragmentFavoriteBinding

/** Recyclerview ListAdapter with DiffUtil for photos in the 'Favorites' fragment **/

class FavoritePhotoAdapter(private val lifecycleOwner: LifecycleOwner, private val viewModel: FavoritesViewModel, private val onClickListener: (Photo, Int) -> Unit)
    : ListAdapter<Photo, FavoritePhotoAdapter.ViewHolder>(DiffCallback) {

    class ViewHolder(private val binding: ListItemFragmentFavoriteBinding, private val lifecycleOwner: LifecycleOwner, private val viewModel: FavoritesViewModel) : RecyclerView.ViewHolder(binding.root) {
        fun bind(photo: Photo) {
            binding.photo = photo
            binding.lifecycleOwner = lifecycleOwner
            binding.favoritesViewModel = viewModel
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(ListItemFragmentFavoriteBinding.inflate(LayoutInflater.from(parent.context), parent, false), lifecycleOwner, viewModel)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val photo: Photo = getItem(position)
        holder.apply {
            itemView.setOnClickListener { onClickListener(photo, position) }
            bind(photo)
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Photo>() {
        override fun areItemsTheSame(oldItem: Photo, newItem: Photo) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Photo, newItem: Photo) = oldItem == newItem
    }

}