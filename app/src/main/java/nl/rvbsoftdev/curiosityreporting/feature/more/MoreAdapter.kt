package nl.rvbsoftdev.curiosityreporting.feature.more

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import nl.rvbsoftdev.curiosityreporting.databinding.FragmentMoreListItemBinding

/** Recyclerview ListAdapter for MoreItems in the 'More' fragment **/

class MoreAdapter(private val onClickListener: OnClickListener) : ListAdapter<MoreItem, MoreAdapter.ViewHolder>(DiffCallback) {

    class ViewHolder(private var binding: FragmentMoreListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(moreItem: MoreItem) {
            binding.moreItem = moreItem
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(FragmentMoreListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val moreItem: MoreItem = getItem(position)
        holder.itemView.setOnClickListener {
            onClickListener.onClick(moreItem)
        }
        holder.bind(moreItem)
    }

    class OnClickListener(val clickListener: (moreItem: MoreItem) -> Unit) {
        fun onClick(moreItem: MoreItem) = clickListener(moreItem)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<MoreItem>() {
        override fun areItemsTheSame(oldItem: MoreItem, newItem: MoreItem) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: MoreItem, newItem: MoreItem) = oldItem == newItem
    }
}