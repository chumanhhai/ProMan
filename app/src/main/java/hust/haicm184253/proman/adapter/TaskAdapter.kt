package hust.haicm184253.proman.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hust.haicm184253.proman.R
import hust.haicm184253.proman.model.Board
import hust.haicm184253.proman.model.Task
import hust.haicm184253.proman.model.User
import hust.haicm184253.proman.utils.Utils
import java.lang.StringBuilder

class TaskAdapter(val context: Context,
                  val itemsList: ArrayList<Task>,
                  val members: ArrayList<User>) : RecyclerView.Adapter<TaskAdapter.ViewHolder>() {

    val hashMap = HashMap<String, User>()
    var taskItemOnClickListener: TaskItemOnClickListener

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val self = itemView
        val taskName = itemView.findViewById<TextView>(R.id.tv_item_task_name)
        val pbPercent = itemView.findViewById<ProgressBar>(R.id.pb_item_task_percent)
        val tvPercent = itemView.findViewById<TextView>(R.id.tv_item_task_percent)
        val ivAddMember = itemView.findViewById<ImageView>(R.id.iv_task_add_member)
        val ivRemoveMember = itemView.findViewById<ImageView>(R.id.iv_task_remove_member)
        val ivRemove = itemView.findViewById<ImageView>(R.id.iv_task_remove)
        val rvMemberList = itemView.findViewById<RecyclerView>(R.id.rv_member_image)
    }

    interface TaskItemOnClickListener {
        fun taskItem(idx: Int, task: Task, taskMembers: ArrayList<User>)
        fun taskItemRemove(task: Task)
        fun taskItemRemoveMember(idx: Int, taskMembers: ArrayList<User>)
        fun taskItemAddMember(idx: Int, taskMembers: ArrayList<User>)
    }

    init {
        for(member in members)
            hashMap[member.uid!!] = member
        taskItemOnClickListener = context as TaskItemOnClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task = itemsList.get(position)
        // set name
        holder.taskName.text = task.name
        // set percent
        val percent = task.percent!!
        holder.pbPercent.progress = percent
        holder.tvPercent.text = StringBuilder("$percent%")

        // set member image list
        val membersList = ArrayList<User>()
        for(memberId in task.assignedUsers!!)
            if(hashMap[memberId] != null)
                membersList.add(hashMap[memberId]!!)
        holder.rvMemberList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        holder.rvMemberList.adapter = MemberImageAdapter(context, membersList)

        // set on click
        holder.self.setOnClickListener {
            taskItemOnClickListener.taskItem(position, task, membersList)
        }
        holder.ivRemove.setOnClickListener {
            taskItemOnClickListener.taskItemRemove(task)
        }
        holder.ivRemoveMember.setOnClickListener {
            taskItemOnClickListener.taskItemRemoveMember(position, membersList)
        }
        holder.ivAddMember.setOnClickListener {
            taskItemOnClickListener.taskItemAddMember(position, membersList)
        }
    }

    override fun getItemCount(): Int {
        return  itemsList.size
    }

}