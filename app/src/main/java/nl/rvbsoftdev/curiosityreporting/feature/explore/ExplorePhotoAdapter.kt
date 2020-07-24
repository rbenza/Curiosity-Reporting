package nl.rvbsoftdev.curiosityreporting.feature.explore

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import nl.rvbsoftdev.curiosityreporting.data.Photo
import nl.rvbsoftdev.curiosityreporting.databinding.ListItemFragmentExploreBinding

/** Recyclerview ListAdapter with DiffUtil for photos in the 'Explore' fragment **/

class ExplorePhotoAdapter(private val lifecycleOwner: LifecycleOwner, private val exploreViewModel: ExploreViewModel, private val onClickListener: (Photo, Int) -> Unit)
    : ListAdapter<Photo, ExplorePhotoAdapter.ViewHolder>(DiffCallback) {

    class ViewHolder(private val binding: ListItemFragmentExploreBinding, private val lifecycleOwner: LifecycleOwner, private val exploreViewModel: ExploreViewModel) : RecyclerView.ViewHolder(binding.root) {
        fun bind(photo: Photo) {
            binding.photo = photo
            binding.lifecycleOwner = lifecycleOwner
            binding.viewModel = exploreViewModel
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(ListItemFragmentExploreBinding.inflate(LayoutInflater.from(parent.context), parent, false), lifecycleOwner, exploreViewModel)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val photo: Photo = getItem(position)
        holder.apply {
            itemView.setOnClickListener {
                onClickListener(photo, position)
            }
            bind(photo)
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Photo>() {
        override fun areItemsTheSame(oldItem: Photo, newItem: Photo) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Photo, newItem: Photo) = oldItem == newItem
    }
}
