package hust.haicm184253.proman.activity

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import hust.haicm184253.proman.R
import hust.haicm184253.proman.adapter.MemberAdapter
import hust.haicm184253.proman.model.Task
import hust.haicm184253.proman.model.User
import hust.haicm184253.proman.network.TaskAPI
import hust.haicm184253.proman.utils.Utils
import java.lang.Exception
import java.lang.reflect.Member

class TaskInfoActivity : BaseActivity(), MemberAdapter.MemberItemOnClickListener {

    lateinit var tb: Toolbar
    lateinit var tvName: TextView
    lateinit var tvDescription: TextView
    lateinit var tvTotalMember: TextView
    lateinit var tvStatus: TextView
    lateinit var rvMember: RecyclerView
    lateinit var btnChangeProgress: ExtendedFloatingActionButton

    lateinit var task: Task
    lateinit var members: ArrayList<User>
    var idx: Int = -1

    var taskChanged = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_info)

        // set view
        tb = findViewById(R.id.tb_task_info_activity)
        tvName = findViewById(R.id.tv_task_info_name)
        tvDescription = findViewById(R.id.tv_task_info_description)
        tvTotalMember = findViewById(R.id.tv_task_info_total_members)
        tvStatus = findViewById(R.id.tv_task_info_status)
        rvMember = findViewById(R.id.rv_task_info_members)
        btnChangeProgress = findViewById(R.id.btn_change_task_progress)

        // get  data from intent
        task = intent.getParcelableExtra(Utils.SEND_TASK)!!
        members = intent.getParcelableArrayListExtra(Utils.SEND_MEMBER)!!
        idx = intent.getIntExtra(Utils.SEND_OBJECT_INDEX, -1)

        // set toolbar
        setToolBar()

        // set UI
        setUI()
    }

    private fun setToolBar() {
        setSupportActionBar(tb)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = task.name
        tb.setNavigationIcon(R.drawable.ic_back_indicator_text_primary)
        tb.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        if(!taskChanged)
            setResult(Activity.RESULT_CANCELED)
        else {
            val intent = Intent()
            intent.putExtra(Utils.SEND_TASK, task)
            intent.putExtra(Utils.SEND_OBJECT_INDEX, idx)
            setResult(Activity.RESULT_OK, intent)
        }
        super.onBackPressed()
    }

    fun setUI() {
        tvName.text = task.name
        tvDescription.text = task.description
        tvTotalMember.text = task.assignedUsers!!.size.toString()
        tvStatus.text = StringBuffer("${task.percent}%")

        // set member list
        rvMember.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvMember.adapter = MemberAdapter(this, members)

        // set on click
        btnChangeProgress.setOnClickListener {
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.dialog_change_task_progress)
            val etProgress = dialog.findViewById<EditText>(R.id.et_task_progress)
            val btnSave = dialog.findViewById<Button>(R.id.btn_save_change_task_progress)

            // set full width of parent
            dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            dialog.show()

            btnSave.setOnClickListener {
                dialog.dismiss()
                try {
                    val percent = etProgress.text.toString().toDouble().toInt()
                    if(percent < 0 || percent > 100)
                        throw Exception()
                    // show waiting dialog
                    showWaitingDialog()
                    val hashMap = hashMapOf<String, Any>(
                        Utils.TASK_ATTR_PERCENT to percent
                    )
                    TaskAPI.updateTask(task.uid!!, hashMap, {
                        dismissWaitingDialog()
                        showToast("Progress updated successfully.")
                        task.percent = percent
                        taskChanged = true
                        tvStatus.text = StringBuffer("${percent}%")
                    }, {
                        showSnackBar(resources.getString(R.string.went_wrong), R.color.colorRedWarning)
                    })
                } catch (e: Exception) {
                    showSnackBar("Invalid value, please try again", R.color.colorRedWarning)
                }
            }
        }
    }

    override fun memberItemOnClick(user: User) {
        val intent = Intent(this, MemberInfoActivity::class.java)
        intent.putExtra(Utils.SEND_USER, user)
        startActivity(intent)
    }
}