package hust.haicm184253.proman.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import de.hdodenhof.circleimageview.CircleImageView
import hust.haicm184253.proman.R
import hust.haicm184253.proman.adapter.BoardAdapter
import hust.haicm184253.proman.model.Board
import hust.haicm184253.proman.model.User
import hust.haicm184253.proman.network.BoardAPI
import hust.haicm184253.proman.network.UserAPI
import hust.haicm184253.proman.utils.Utils

class MainActivity : BaseActivity(), View.OnClickListener, BoardAdapter.BoardItemOnClickListener {

    lateinit var tbMainActivity: Toolbar
    lateinit var dlMainActivity: DrawerLayout
    lateinit var nvMainActivity: NavigationView
    lateinit var civUserImagePopup: CircleImageView
    lateinit var tvUserNamePopup: TextView
    lateinit var llMainContent: LinearLayout
    lateinit var fabCreateBoard: FloatingActionButton
    lateinit var tvNoBoards: TextView
    lateinit var rvBoards: RecyclerView
    lateinit var srlMainActivity: SwipeRefreshLayout

    lateinit var user: User
    lateinit var boards: ArrayList<Board>
    lateinit var adapter: BoardAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // set view
        tbMainActivity = findViewById(R.id.tb_main_activity)
        dlMainActivity = findViewById(R.id.dl_main_activity)
        nvMainActivity = findViewById(R.id.nv_main_activity)
        val headerLayout = nvMainActivity.getHeaderView(0)
        civUserImagePopup = headerLayout.findViewById(R.id.civ_user_image_popup)
        tvUserNamePopup = headerLayout.findViewById(R.id.tv_user_name_popup)
        llMainContent = findViewById(R.id.ll_content_main)
        fabCreateBoard = findViewById(R.id.fab_create_board)
        tvNoBoards = findViewById(R.id.tv_no_boards)
        rvBoards = findViewById(R.id.rv_board)
        srlMainActivity = findViewById(R.id.srl_main_activity)

        // set action bar
        setToolBar()

        // get data
        getData()

        // set on click
        fabCreateBoard.setOnClickListener(this)

        // set swipe to refresh
        setSwipeToRefresh()
    }

    fun  setToolBar() {
        setSupportActionBar(tbMainActivity)
        tbMainActivity.setNavigationIcon(R.drawable.ic_menu_main_activity)
        tbMainActivity.setNavigationOnClickListener {
            dlMainActivity.openDrawer(Gravity.LEFT)
        }
        nvMainActivity.setNavigationItemSelectedListener { menuItem ->
            when(menuItem.itemId) {
                R.id.menu_item_main_profile -> {
                    val intent = Intent(this, MyProfileActivity::class.java)
                    intent.putExtra(Utils.SEND_USER, user)
                    startActivityForResult(intent, Utils.GET_USER_REQUEST_CODE)
                    dlMainActivity.closeDrawer(Gravity.LEFT)
                }
                R.id.menu_item_main_sign_out -> {
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(this, IntroActivity::class.java)
                    startActivity(intent)
                }
            }
            true
        }
    }

    fun getData() {
        srlMainActivity.isRefreshing = true
        // get user data
        UserAPI.getCurrentUser({ userResult: User ->
            // set user UI
            setUserUI(userResult)
            // set boards data
            BoardAPI.getAllBoardsOfCurrentUser({ boards ->
                // set main content
                this.boards = boards
                rvBoards.layoutManager = LinearLayoutManager(this)
                adapter = BoardAdapter(this, boards, user.name!!)
                rvBoards.adapter = adapter
                setMainContentUI()
            }, {
                showSnackBar(resources.getString(R.string.went_wrong), R.color.colorRedWarning)
            })
        }, {
            showSnackBar(resources.getString(R.string.went_wrong), R.color.colorRedWarning)
        })

    }

    fun setMainContentUI() {
        srlMainActivity.isRefreshing = false
        if(boards.size == 0) {
            tvNoBoards.visibility = View.VISIBLE
            rvBoards.visibility = View.GONE
        } else {
            tvNoBoards.visibility = View.GONE
            rvBoards.visibility = View.VISIBLE
        }
    }

    fun setUserUI(user: User) {
        this.user = user

        // set properties
        tvUserNamePopup.text = user.name
        user.image?.let {
            Utils.setImageUrl(this, civUserImagePopup, user.image, R.drawable.img_profile)
        }
    }

    override fun onClick(v: View?) {
        when(v!!.id) {
            R.id.fab_create_board -> {
                val intent = Intent(this, CreateBoardActivity::class.java)
                startActivityForResult(intent, Utils.GET_BOARD_REQUEST_CODE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK) {
            when(requestCode) {
                Utils.GET_USER_REQUEST_CODE -> {
                    // set user UI
                    setUserUI(data!!.getParcelableExtra(Utils.SEND_USER)!!)
                }
                Utils.GET_BOARD_REQUEST_CODE -> {
                    val board = data!!.getParcelableExtra<Board>(Utils.SEND_BOARD)!!
                    boards.add(0, board)
                    adapter.notifyDataSetChanged()
                    setMainContentUI()
                }
                Utils.BOARD_CHANGE_REQUEST_CODE -> {
                    loadBoards()
                }
            }
        }
    }

    fun loadBoards() {
        srlMainActivity.isRefreshing = true
        BoardAPI.getAllBoardsOfCurrentUser({boards ->
            this.boards = boards
            setMainContentUI()
            adapter.notifyDataSetChanged()
            srlMainActivity.isRefreshing = false
        }, {
            showSnackBar(resources.getString(R.string.went_wrong), R.color.colorRedWarning)
        })
    }

    override fun boardItemOnClick(board: Board) {
        val intent = Intent(this, BoardActivity::class.java)
        intent.putExtra(Utils.SEND_BOARD, board)
        startActivityForResult(intent, Utils.BOARD_CHANGE_REQUEST_CODE)
    }

    fun setSwipeToRefresh() {
        srlMainActivity.setOnRefreshListener {
            loadBoards()
        }
    }
}