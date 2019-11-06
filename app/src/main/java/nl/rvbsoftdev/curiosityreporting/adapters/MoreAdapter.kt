package nl.rvbsoftdev.curiosityreporting.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import nl.rvbsoftdev.curiosityreporting.R

/** Legacy ListView with findViewById for demo purpose. It's a static list with 4 items so no performance from RecyclerView DiffUtil required **/

class ListViewAdapter(var context: Context, var moreItems: ArrayList<MoreItems>) : BaseAdapter() {

    class ViewHolder(row: View) {
        var icon: ImageView
        var textTitle: TextView
        var textSubtitle: TextView

        init {
            this.icon = row.findViewById(R.id.icon_more_list) as ImageView
            this.textTitle = row.findViewById(R.id.text_header_more_list) as TextView
            this.textSubtitle = row.findViewById(R.id.text_detail_more_list) as TextView
        }
    }

    override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup?): View {

        val view: View?
        val viewHolder: ViewHolder
        if (convertView == null) {
            val layout = LayoutInflater.from(context)
            view = layout.inflate(R.layout.fragment_more_list_item, convertView, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder

        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        val moreItems: MoreItems = getItem(position) as MoreItems
        viewHolder.icon.setImageResource(moreItems.icon)
        viewHolder.textTitle.text = moreItems.textTitle
        viewHolder.textSubtitle.text = moreItems.textSubTitle
        return view as View
    }

    override fun getItem(position: Int): Any {
        return moreItems[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return 4
    }
}

data class MoreItems(var id: Int, var icon: Int, var textTitle: String, var textSubTitle: String)








