package hust.haicm184253.proman.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.hdodenhof.circleimageview.CircleImageView
import hust.haicm184253.proman.R
import hust.haicm184253.proman.model.Board
import hust.haicm184253.proman.model.User
import hust.haicm184253.proman.utils.Utils

class MemberAdapter(val context: Context, val itemsList: ArrayList<User>) : RecyclerView.Adapter<MemberAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val civImage = itemView.findViewById<CircleImageView>(R.id.civ_item_member)
        val tvName = itemView.findViewById<TextView>(R.id.tv_item_member_name)
        val tvEmail = itemView.findViewById<TextView>(R.id.tv_item_member_email)
        val self = itemView
    }

    interface MemberItemOnClickListener {
        fun memberItemOnClick(user: User)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_member, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = itemsList.get(position)
        // set image
        Utils.setImageUrl(context, holder.civImage, user.image, R.drawable.img_work)
        // set name
        holder.tvName.text = user.name
        // set email
        holder.tvEmail.text = user.email
        // set on click
        holder.self.setOnClickListener {
            val memberItemOnClickListener = context as MemberItemOnClickListener
            memberItemOnClickListener.memberItemOnClick(user)
        }
    }

    override fun getItemCount(): Int {
        return  itemsList.size
    }

}