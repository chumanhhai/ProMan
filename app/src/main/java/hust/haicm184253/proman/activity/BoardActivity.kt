package hust.haicm184253.proman.activity

import android.app.ActionBar
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import hust.haicm184253.proman.R
import hust.haicm184253.proman.adapter.TaskAdapter
import hust.haicm184253.proman.model.Board
import hust.haicm184253.proman.model.Task
import hust.haicm184253.proman.model.User
import hust.haicm184253.proman.network.BoardAPI
import hust.haicm184253.proman.network.TaskAPI
import hust.haicm184253.proman.network.UserAPI
import hust.haicm184253.proman.utils.Utils

class BoardActivity : BaseActivity(), View.OnClickListener, TaskAdapter.TaskItemOnClickListener {

    lateinit var tb: Toolbar
    lateinit var tvNoTasks: TextView
    lateinit var rvTasks: RecyclerView
    lateinit var btnCreate: ExtendedFloatingActionButton
    lateinit var srlBoardActivity: SwipeRefreshLayout

    lateinit var board: Board
    lateinit var members: ArrayList<User>
    lateinit var host: User
    lateinit var tasks: ArrayList<Task>

    var boardChanged = false

    lateinit var adapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board)

        // set view
        tb = findViewById(R.id.tb_board_activity)
        tvNoTasks = findViewById(R.id.tv_no_tasks)
        rvTasks = findViewById(R.id.rv_task)
        btnCreate = findViewById(R.id.btn_create_task)
        srlBoardActivity = findViewById(R.id.srl_board_activity)

        // get board
        board = intent.getParcelableExtra(Utils.SEND_BOARD)!!

        // set tool bar
        setToolBar()

        // get data
        getData()

        // set on click
        btnCreate.setOnClickListener(this)

        // set swipe to refresh
        setSwipeToRefresh()
    }

    fun setSwipeToRefresh() {
        srlBoardActivity.setOnRefreshListener {
            getData()
        }
    }

    private fun setMainUI() {
        srlBoardActivity.isRefreshing = false
        if(tasks.size == 0) {
            tvNoTasks.visibility = View.VISIBLE
            rvTasks.visibility = View.GONE
        } else {
            tvNoTasks.visibility = View.GONE
            rvTasks.visibility = View.VISIBLE
        }
    }

    private fun setToolBar() {
        setSupportActionBar(tb)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = board.name
        tb.setNavigationIcon(R.drawable.ic_back_indicator_white)
        tb.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        if(!boardChanged)
            setResult(Activity.RESULT_CANCELED)
        else {
            setResult(Activity.RESULT_OK)
        }
        super.onBackPressed()
    }

    private fun getData() {
        srlBoardActivity.isRefreshing = true

        val failureCallback: () -> Unit = {
            showSnackBar(resources.getString(R.string.went_wrong), R.color.colorRedWarning)
        }

        // get all members
        UserAPI.getAllMembersOfBoard(board.assignedUsers!!, { users ->
            members = users
            // get host
            for(user in members)
                if(user.uid == board.createdBy) {
                    host = user
                    break
                }

            // get all tasks
            TaskAPI.getAllTasksOfBoard(board.uid!!, { tasks ->
                this.tasks = tasks

                // set task list
                rvTasks.layoutManager = LinearLayoutManager(this)
                adapter = TaskAdapter(this, tasks, members)
                rvTasks.adapter = adapter

                // set UI
                setMainUI()
            }, failureCallback)
        }, failureCallback)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_board_activity, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_board_info -> {
                val intent = Intent(this, BoardInfoActivity::class.java)
                intent.putExtra(Utils.SEND_BOARD, board)
                startActivity(intent)
            }
            R.id.action_board_member -> {
                val intent = Intent(this, MemberListActivity::class.java)
                intent.putExtra(Utils.SEND_HOST, host)
                intent.putExtra(Utils.SEND_MEMBER, members)
                intent.putExtra(Utils.SEND_BOARD, board)
                startActivityForResult(intent, Utils.MEMBER_CHANGE_REQUEST_CODE)
            }
            R.id.action_board_finish -> {
                AlertDialog.Builder(this)
                        .setTitle("Are your sure want to Finish this project?")
                        .setPositiveButton("Finish", DialogInterface.OnClickListener { dialog, which ->
                            board.finished = true
                            boardChanged = true
                            dialog.dismiss()

                            // show waiting dialog
                            showWaitingDialog()
                            val update = hashMapOf<String, Any>(
                                    Utils.BOARD_ATTR_FINISHED to board.finished!!
                            )
                            // update online
                            BoardAPI.updateBoardById(board.uid!!, update, {
                                showToast("Congratulation! You have finished a project.")
                                onBackPressed()
                            }, {
                                showSnackBar(resources.getString(R.string.went_wrong), R.color.colorRedWarning)
                            })
                        })
                        .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which ->
                            dialog.dismiss()
                        })
                        .show()
            }
            R.id.action_board_delete -> {
                AlertDialog.Builder(this)
                        .setTitle("Are your sure want to Delete this project?")
                        .setPositiveButton("Delete", DialogInterface.OnClickListener { dialog, which ->
                            // delete board
                            showWaitingDialog()
                            dialog.dismiss()
                            BoardAPI.deleteBoardById(board.uid!!, {
                                boardChanged = true
                                showToast("You have deleted a project.")
                                onBackPressed()
                                dismissWaitingDialog()
                            }, {
                                showSnackBar(resources.getString(R.string.went_wrong), R.color.colorRedWarning)
                            })
                        })
                        .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which ->
                            dialog.dismiss()
                        })
                        .show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK) {
            when(requestCode) {
                Utils.MEMBER_CHANGE_REQUEST_CODE -> {
                    members = data!!.getParcelableArrayListExtra(Utils.SEND_MEMBER)!!
                    board = data.getParcelableExtra(Utils.SEND_BOARD)!!
                    boardChanged = true
                }
                Utils.GET_TASK_REQUEST_CODE -> {
                    val task = data!!.getParcelableExtra<Task>(Utils.SEND_TASK)
                    tasks.add(0, task!!)
                    adapter.notifyDataSetChanged()
                    setMainUI()
                }
                Utils.GET_TASK_FOR_UPDATE_REQUEST_CODE -> {
                    val task = data!!.getParcelableExtra<Task>(Utils.SEND_TASK)!!
                    val idx = data.getIntExtra(Utils.SEND_OBJECT_INDEX, -1)
                    if(idx != -1) {
                        tasks.set(idx, task)
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when(v!!.id) {
            R.id.btn_create_task -> {
                val intent = Intent(this, CreateTaskActivity::class.java)
                intent.putExtra(Utils.SEND_ID, board.uid)
                intent.putExtra(Utils.SEND_MEMBER, members)
                startActivityForResult(intent, Utils.GET_TASK_REQUEST_CODE)
            }
        }
    }

    override fun taskItem(idx: Int, task: Task, taskMembers: ArrayList<User>) {
        val intent = Intent(this, TaskInfoActivity::class.java)
        intent.putExtra(Utils.SEND_OBJECT_INDEX, idx)
        intent.putExtra(Utils.SEND_TASK, task)
        intent.putExtra(Utils.SEND_MEMBER, taskMembers)
        startActivityForResult(intent, Utils.GET_TASK_FOR_UPDATE_REQUEST_CODE)
    }

    override fun taskItemRemove(task: Task) {
        // show alert
        AlertDialog.Builder(this)
                .setTitle("Are you sure want to DELETE task?")
                .setPositiveButton("Delete", DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss()
                    // show waiting dialog
                    showWaitingDialog()
                    // delete task
                    TaskAPI.deleteTask(task.uid!!, {
                        tasks.remove(task)
                        showToast("Task removed successfully.")
                        dismissWaitingDialog()
                        adapter.notifyDataSetChanged()
                        setMainUI()
                    }, {
                        showToast(resources.getString(R.string.went_wrong))
                    })
                })
                .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss()
                })
                .show()
    }

    override fun taskItemRemoveMember(idx: Int, taskMembers: ArrayList<User>) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_task_remove_member)
        val etRemove = dialog.findViewById<AutoCompleteTextView>(R.id.et_remove_member)
        val btnRemove = dialog.findViewById<Button>(R.id.btn_remove_member)
        // set full width of screen
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.show()

        // set adapter
        val memberEmails = Utils.getEmailsFromUsers(taskMembers)
        val autoCompleteAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, memberEmails)
        etRemove.setAdapter(autoCompleteAdapter)

        // set on click
        btnRemove.setOnClickListener {
            dialog.dismiss()
            val email = etRemove.text.toString()
            when {
                email.isEmpty() -> {
                    showSnackBar("Email must not be empty.", R.color.colorRedWarning)
                }
                !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    showSnackBar("Email is invalid.", R.color.colorRedWarning)
                }
                else -> {
                    var id: String? = null
                    // check if email is in task member emails
                    for(member in taskMembers)
                        if(member.email!! == email) {
                            id = member.uid!!
                            break
                        }
                    if(id == null)
                        showSnackBar("'${email}' is not in task.", R.color.colorRedWarning)
                    else {
                        // show waiting dialog
                        showWaitingDialog()

                        tasks.get(idx).assignedUsers!!.remove(id)
                        val hashMap = hashMapOf<String, Any>(
                                Utils.TASK_ATTR_ASSIGNED_USERS to tasks.get(idx).assignedUsers!!
                        )

                        TaskAPI.updateTask(tasks.get(idx).uid!!, hashMap, {
                            dismissWaitingDialog()
                            showToast("'${email}' removed successfully.")
                            adapter.notifyDataSetChanged()
                        }, {
                            dismissWaitingDialog()
                            showSnackBar(resources.getString(R.string.went_wrong), R.color.colorRedWarning)
                        })
                    }
                }
            }
        }
    }

    override fun taskItemAddMember(idx: Int, taskMembers: ArrayList<User>) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_task_add_member)
        val etRemove = dialog.findViewById<AutoCompleteTextView>(R.id.et_add_member)
        val btnRemove = dialog.findViewById<Button>(R.id.btn_add_member)
        // set full width of screen
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.show()

        // set adapter
        val memberEmails = Utils.getEmailsFromUsers(members)
        val autoCompleteAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, memberEmails)
        etRemove.setAdapter(autoCompleteAdapter)

        // set on click
        btnRemove.setOnClickListener {
            dialog.dismiss()
            val email = etRemove.text.toString()
            when {
                email.isEmpty() -> {
                    showSnackBar("Email must not be empty.", R.color.colorRedWarning)
                }
                !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    showSnackBar("Email is invalid.", R.color.colorRedWarning)
                }
                else -> {
                    var id: String? = null
                    // check if email is in member emails
                    for(member in members)
                        if(member.email!! == email) {
                            id = member.uid!!
                            break
                        }
                    if(id == null)
                        showSnackBar("'${email}' is not in project.", R.color.colorRedWarning)
                    else {
                        // check if added member is already in task members
                        var flag = false
                        for(taskMember in taskMembers)
                            if(taskMember.uid!! == id) {
                                flag = true
                                break
                            }

                        if(flag)
                            showSnackBar("'${email}' is already in task.", R.color.colorRedWarning)
                        else {
                            // show waiting dialog
                            showWaitingDialog()

                            tasks.get(idx).assignedUsers!!.add(id)
                            val hashMap = hashMapOf<String, Any>(
                                    Utils.TASK_ATTR_ASSIGNED_USERS to tasks.get(idx).assignedUsers!!
                            )

                            TaskAPI.updateTask(tasks.get(idx).uid!!, hashMap, {
                                dismissWaitingDialog()
                                showToast("'${email}' added successfully.")
                                adapter.notifyDataSetChanged()
                            }, {
                                dismissWaitingDialog()
                                showSnackBar(resources.getString(R.string.went_wrong), R.color.colorRedWarning)
                            })
                        }
                    }
                }
            }
        }
    }
}