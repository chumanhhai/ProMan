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
import hust.haicm184253.proman.utils.Utils

class BoardAdapter(val context: Context, val itemsList: ArrayList<Board>, val userName: String) : RecyclerView.Adapter<BoardAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val civImage = itemView.findViewById<CircleImageView>(R.id.civ_item_board)
        val tvBoardName = itemView.findViewById<TextView>(R.id.tv_item_board_board_name)
        val tvOwnerName = itemView.findViewById<TextView>(R.id.tv_item_board_owner_name)
        val tvStatusContinue = itemView.findViewById<TextView>(R.id.tv_item_board_status_continue)
        val tvStatusFinished = itemView.findViewById<TextView>(R.id.tv_item_board_status_finished)
        val self = itemView
    }

    interface BoardItemOnClickListener {
        fun boardItemOnClick(board: Board)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_board, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val board = itemsList.get(position)
        // set image
        Utils.setImageUrl(context, holder.civImage, board.image, R.drawable.img_work)
        // set board name
        holder.tvBoardName.text = board.name
        // set owner name
        holder.tvOwnerName.text = "created by " + userName
        // set status
        if(board.finished!!) {
            holder.tvStatusContinue.visibility = View.GONE
            holder.tvStatusFinished.visibility = View.VISIBLE
        } else {
            holder.tvStatusContinue.visibility = View.VISIBLE
            holder.tvStatusFinished.visibility = View.GONE
        }
        // set on click
        holder.self.setOnClickListener {
            val boardItemOnClickListener = context as BoardItemOnClickListener
            boardItemOnClickListener.boardItemOnClick(board)
        }
    }

    override fun getItemCount(): Int {
        return  itemsList.size
    }

}