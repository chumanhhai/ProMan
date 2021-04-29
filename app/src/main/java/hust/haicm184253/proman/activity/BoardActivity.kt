package hust.haicm184253.proman.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

class BoardActivity : BaseActivity(), View.OnClickListener {

    lateinit var tb: Toolbar
    lateinit var pbLoading: ProgressBar
    lateinit var llMainContent: LinearLayout
    lateinit var tvNoTasks: TextView
    lateinit var rvTasks: RecyclerView
    lateinit var btnCreate: ExtendedFloatingActionButton

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
        pbLoading = findViewById(R.id.pb_board_activity_loading)
        llMainContent = findViewById(R.id.ll_board_activity)
        tvNoTasks = findViewById(R.id.tv_no_tasks)
        rvTasks = findViewById(R.id.rv_task)
        btnCreate = findViewById(R.id.btn_create_task)

        // get board
        board = intent.getParcelableExtra(Utils.SEND_BOARD)!!

        // set tool bar
        setToolBar()

        // get data
        getData()

        // set on click
        btnCreate.setOnClickListener(this)

    }

    private fun unLoadMainContent() {
        pbLoading.visibility = View.GONE
        llMainContent.visibility = View.VISIBLE
    }

    private fun setMainUI() {
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
                unLoadMainContent()
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
}