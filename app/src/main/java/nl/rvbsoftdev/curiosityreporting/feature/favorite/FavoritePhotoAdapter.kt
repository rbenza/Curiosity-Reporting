package nl.rvbsoftdev.curiosityreporting.feature.favorite

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import nl.rvbsoftdev.curiosityreporting.data.Photo
import nl.rvbsoftdev.curiosityreporting.databinding.FragmentFavoriteRecyclerviewItemBinding

/** Recyclerview ListAdapter with DiffUtil for photos in the 'Favorites' fragment **/

class FavoritePhotoAdapter(private val onClickListener: OnClickListener) : ListAdapter<Photo, FavoritePhotoAdapter.ViewHolder>(DiffCallback) {

    class ViewHolder(private var dataBinding: FragmentFavoriteRecyclerviewItemBinding) : RecyclerView.ViewHolder(dataBinding.root) {
        fun bind(photo: Photo) {
            dataBinding.photo = photo
            dataBinding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(FragmentFavoriteRecyclerviewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val photo: Photo = getItem(position)
        holder.apply {
            itemView.setOnClickListener { onClickListener.onClick(photo) }
            bind(photo)
        }
    }

    class OnClickListener(val clickListener: (photo: Photo) -> Unit) {
        fun onClick(photo: Photo) = clickListener(photo)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Photo>() {
        override fun areItemsTheSame(oldItem: Photo, newItem: Photo) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Photo, newItem: Photo) = oldItem == newItem
    }
}
