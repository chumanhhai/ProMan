package hust.haicm184253.proman.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import hust.haicm184253.proman.R
import hust.haicm184253.proman.model.Task
import hust.haicm184253.proman.model.User
import hust.haicm184253.proman.network.TaskAPI
import hust.haicm184253.proman.utils.Utils
import java.util.*
import kotlin.collections.ArrayList

class CreateTaskActivity : BaseActivity(), View.OnClickListener {

    lateinit var tb: Toolbar
    lateinit var etName: EditText
    lateinit var etDescription: EditText
    lateinit var etAssignTo: MultiAutoCompleteTextView
    lateinit var btnCreate: Button

    lateinit var members: ArrayList<User>
    lateinit var boardId: String
    lateinit var memberEmails: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_task)

        // set view
        tb = findViewById(R.id.tb_create_task)
        etName = findViewById(R.id.et_task_name)
        etDescription = findViewById(R.id.et_task_description)
        etAssignTo = findViewById(R.id.et_task_assign_users)
        btnCreate = findViewById(R.id.btn_task_create_task)

        // set tool bar
        setToolBar()

        // get data from intent
        members = intent.getParcelableArrayListExtra(Utils.SEND_MEMBER)!!
        memberEmails = Utils.getEmailsFromUsers(members)
        boardId = intent.getStringExtra(Utils.SEND_ID)!!

        // set autocomplete assign to
        setAutoCompleteAssign()

        // set on click
        btnCreate.setOnClickListener(this)
    }

    fun setToolBar() {
        setSupportActionBar(tb)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_back_indicator_text_primary)
        tb.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    fun setAutoCompleteAssign() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, memberEmails)
        etAssignTo.setAdapter(adapter)
        etAssignTo.setTokenizer(MultiAutoCompleteTextView.CommaTokenizer())
    }

    override fun onClick(v: View?) {
        when(v!!.id) {
            R.id.btn_task_create_task -> {
                // clost key board
                Utils.closeKeyboard(this)

                val name = etName.text.toString()
                val description = etDescription.text.toString()
                var assignToEmails = etAssignTo.text.toString().replace(" ", "")

                // process assignToEmails text
                if(assignToEmails.last() != ',')
                    assignToEmails = "$assignToEmails,"
                val assignToEmailsList = assignToEmails.split(",") as ArrayList<String>
                assignToEmailsList.removeLast()

                val assignTo = ArrayList<String>()
                val fromEmailsToIds = Utils.fromEmailsToIdsUsers(members)

                // get list of assignUsers
                for(email in assignToEmailsList)
                    if(fromEmailsToIds[email] == null) {
                        showSnackBar("You can only add members from project. Please be careful to spelling.", R.color.colorRedWarning)
                        return
                    } else
                        assignTo.add(fromEmailsToIds[email]!!)

                when {
                    name.isEmpty() -> {
                        showSnackBar("Name must not be empty.", R.color.colorRedWarning)
                    }
                    description.isEmpty() -> {
                        showSnackBar("Description must not be empty.", R.color.colorRedWarning)
                    }
                    assignTo.size == 0 -> {
                        showSnackBar("Members assigned must not be empty.", R.color.colorRedWarning)
                    }
                    else -> {
                        // show waiting dialog
                        showWaitingDialog()

                        // create task
                        val id = UUID.randomUUID().toString()
                        val task = Task(id, name, description, boardId, 0, assignTo)
                        TaskAPI.addTask(task, {
                            dismissWaitingDialog()
                            showToast("Task created successfully.")
                            val intent = Intent()
                            intent.putExtra(Utils.SEND_TASK, task)
                            setResult(Activity.RESULT_OK, intent)
                            finish()
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