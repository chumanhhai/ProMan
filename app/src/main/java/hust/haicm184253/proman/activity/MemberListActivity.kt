package hust.haicm184253.proman.activity

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hust.haicm184253.proman.R
import hust.haicm184253.proman.adapter.MemberAdapter
import hust.haicm184253.proman.model.Board
import hust.haicm184253.proman.model.User
import hust.haicm184253.proman.network.BoardAPI
import hust.haicm184253.proman.network.UserAPI
import hust.haicm184253.proman.utils.Utils

class MemberListActivity : BaseActivity(), MemberAdapter.MemberItemOnClickListener {

    lateinit var tb: Toolbar
    lateinit var rvMember: RecyclerView
    lateinit var rvHost: RecyclerView
    lateinit var tvNoMember: TextView

    lateinit var adapter: MemberAdapter

    lateinit var board: Board
    lateinit var host: User
    lateinit var members: ArrayList<User>
    lateinit var memberEmails: ArrayList<String>
    var memberChanged = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_member)

        // set view
        tb = findViewById(R.id.tb_member_activity)
        rvMember = findViewById(R.id.rv_member)
        rvHost = findViewById(R.id.rv_host)
        tvNoMember = findViewById(R.id.tv_no_members)

        // set tool bar
        setToolBar()

        // get data from intent
        board = intent.getParcelableExtra(Utils.SEND_BOARD)!!
        host = intent.getParcelableExtra(Utils.SEND_HOST)!!
        members = intent.getParcelableArrayListExtra(Utils.SEND_MEMBER)!!

        // set UI
        setUI()
    }

    private fun setToolBar() {
        setSupportActionBar(tb)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Member"
        tb.setNavigationIcon(R.drawable.ic_back_indicator_white)
        tb.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        if(memberChanged) {
            val intent = Intent()
            intent.putExtra(Utils.SEND_MEMBER, members)
            intent.putExtra(Utils.SEND_BOARD, board)
            setResult(Activity.RESULT_OK, intent)
        } else {
            setResult(Activity.RESULT_CANCELED)
        }
        finish()
        super.onBackPressed()
    }

    private fun setUI() {
        // host
        rvHost.layoutManager = LinearLayoutManager(this)
        rvHost.adapter = MemberAdapter(this, arrayListOf(host))

        // member
        rvMember.layoutManager = LinearLayoutManager(this)
        adapter = MemberAdapter(this, members)
        rvMember.adapter = adapter

        // should set no member text?
        setNoMembersOrNot()
    }

    private fun setNoMembersOrNot() {
        if(members.size == 0) {
            tvNoMember.visibility = View.VISIBLE
            rvMember.visibility = View.GONE
        } else {
            tvNoMember.visibility = View.GONE
            rvMember.visibility = View.VISIBLE
        }
    }

    override fun memberItemOnClick(user: User) {
        val intent = Intent(this, MemberInfoActivity::class.java)
        intent.putExtra(Utils.SEND_USER, user)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_member_list_activity, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_member_list_add_person -> {
                val dialog = Dialog(this)
                dialog.setContentView(R.layout.dialog_board_add_member)
                val etAddPerson = dialog.findViewById<EditText>(R.id.et_add_member)
                val btnAddPerson = dialog.findViewById<Button>(R.id.btn_add_person)
                dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                dialog.show()
                // set on click
                btnAddPerson.setOnClickListener {
                    // dissmis add person dialog
                    dialog.dismiss()
                    // close keyboard
                    Utils.closeKeyboard(this@MemberListActivity)
                    val email = etAddPerson.text.toString()
                    when {
                        email.isEmpty() -> {
                            showSnackBar("Email must not be empty.", R.color.colorRedWarning)
                        }
                        !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                            showSnackBar("Invalid email.", R.color.colorRedWarning)
                        }
                        else -> {
                            showWaitingDialog()
                            // get user info
                            UserAPI.getUserByEmail(email, {user ->
                                dismissWaitingDialog()
                                if(user == null)
                                    showSnackBar("Can not find user", R.color.colorRedWarning)
                                else {
                                    var flag = false;
                                    for(member in members)
                                        if(member.uid == user.uid) {
                                            showSnackBar("'${user.name}' is already in project.", R.color.colorRedWarning)
                                            flag = true
                                            break;
                                        }
                                    // if user is valid
                                    if(!flag) {
                                        board.assignedUsers!!.add(0, user.uid!!)
                                        val update = hashMapOf<String, Any>(
                                            Utils.BOARD_ATTR_ASSIGNED_USERS to board.assignedUsers!!
                                        )
                                        BoardAPI.updateBoardById(board.uid!!, update, {
                                            members.add(0, user)
                                            memberChanged = true
                                            adapter.notifyDataSetChanged()
                                            showToast("'${user.name}' is added successfully.")
                                            setNoMembersOrNot()
                                        }, {
                                            showSnackBar(resources.getString(R.string.went_wrong), R.color.colorRedWarning)
                                        })
                                    }
                                }
                            }, {
                                showSnackBar(resources.getString(R.string.went_wrong), R.color.colorRedWarning)
                            })
                        }
                    }
                }
            }
            R.id.action_member_list_delete_person -> {
                val dialog = Dialog(this)
                dialog.setContentView(R.layout.dialog_board_delete_member)
                val etDeletePerson = dialog.findViewById<AutoCompleteTextView>(R.id.et_delete_member)
                val btnDeletePerson = dialog.findViewById<Button>(R.id.btn_delete_person)

                // set dialog full width and height of screen
                dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                dialog.show()

                // set adapter
                memberEmails = Utils.getEmailsFromUsers(members)
                memberEmails.remove(host.email)
                val autoCompleteAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, memberEmails)
                etDeletePerson.setAdapter(autoCompleteAdapter)

                // set on click
                btnDeletePerson.setOnClickListener {
                    // dissmiss add person dialog
                    dialog.dismiss()
                    // close keyboard
                    Utils.closeKeyboard(this@MemberListActivity)
                    val email = etDeletePerson.text.toString()
                    when {
                        email.isEmpty() -> {
                            showSnackBar("Email must not be empty.", R.color.colorRedWarning)
                        }
                        !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                            showSnackBar("Invalid email.", R.color.colorRedWarning)
                        }
                        else -> {
                            var removedMember: User? = null
                            // check if user is in members
                            for((idx, member) in members.withIndex())
                                if(member.email == email) {
                                    removedMember = member
                                    members.removeAt(idx)
                                    board.assignedUsers!!.remove(member.uid)
                                    memberChanged = true
                                    break;
                                }
                            if(removedMember == null) {
                                showSnackBar("User is not in project.", R.color.colorRedWarning)
                            } else {
                                // show waiting dialog
                                showWaitingDialog()

                                val update = hashMapOf<String, Any>(
                                    Utils.BOARD_ATTR_ASSIGNED_USERS to board.assignedUsers!!
                                )
                                BoardAPI.updateBoardById(board.uid!!, update, {
                                    adapter.notifyDataSetChanged()
                                    showToast("'${removedMember.name}' is removed successfully.")
                                    setNoMembersOrNot()
                                    dismissWaitingDialog()
                                }, {
                                    showSnackBar(resources.getString(R.string.went_wrong), R.color.colorRedWarning)
                                    dismissWaitingDialog()
                                })
                            }
                        }
                    }
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
}