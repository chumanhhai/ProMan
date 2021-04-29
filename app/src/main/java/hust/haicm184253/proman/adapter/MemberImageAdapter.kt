package hust.haicm184253.proman.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import de.hdodenhof.circleimageview.CircleImageView
import hust.haicm184253.proman.R
import hust.haicm184253.proman.model.User
import hust.haicm184253.proman.utils.Utils

class MemberImageAdapter(val context: Context, val itemsList: ArrayList<User>) : RecyclerView.Adapter<MemberImageAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val civImage = itemView.findViewById<CircleImageView>(R.id.civ_item_member_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_member_image, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = itemsList.get(position)
        // set image
        Utils.setImageUrl(context, holder.civImage, user.image, R.drawable.img_work)
    }

    override fun getItemCount(): Int {
        return  itemsList.size
    }

}