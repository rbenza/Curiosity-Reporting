package nl.rvbsoftdev.curiosityreporting.favorite

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import nl.rvbsoftdev.curiosityreporting.databinding.FragmentFavoriteRecyclerviewItemBinding
import nl.rvbsoftdev.curiosityreporting.data.Photo

/** Recyclerview adapter with DiffUtil for photos in the 'Favorites' fragment **/

class FavoritesPhotoAdapter(private val onClickListener: OnClickListener) :
        ListAdapter<Photo, FavoritesPhotoAdapter.FavoritePhotoViewHolder>(DiffCallback) {

    class FavoritePhotoViewHolder(private var dataBinding: FragmentFavoriteRecyclerviewItemBinding) :
            RecyclerView.ViewHolder(dataBinding.root) {

        fun bind(photo: Photo) {
            dataBinding.photo = photo
            dataBinding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Photo>() {
        override fun areItemsTheSame(oldItem: Photo, newItem: Photo): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Photo, newItem: Photo): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): FavoritePhotoViewHolder {
        return FavoritePhotoViewHolder(FragmentFavoriteRecyclerviewItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: FavoritePhotoViewHolder, position: Int) {
        val photo: Photo = getItem(position)
        holder.itemView.setOnClickListener {
            onClickListener.onClick(photo)
        }
        holder.bind(photo)
    }

    class OnClickListener(val clickListener: (photo: Photo) -> Unit) {
        fun onClick(photo: Photo) = clickListener(photo)
    }
}
