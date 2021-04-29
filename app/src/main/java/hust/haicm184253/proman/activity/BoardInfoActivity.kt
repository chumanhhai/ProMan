package hust.haicm184253.proman.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import de.hdodenhof.circleimageview.CircleImageView
import hust.haicm184253.proman.R
import hust.haicm184253.proman.model.Board
import hust.haicm184253.proman.utils.Utils

class BoardInfoActivity : AppCompatActivity() {

    lateinit var tb: Toolbar
    lateinit var tvName: TextView
    lateinit var tvDescription: TextView
    lateinit var tvTotalMembers: TextView
    lateinit var tvStatusContinue: TextView
    lateinit var tvStatusFinished: TextView
    lateinit var civImage: CircleImageView

    lateinit var board: Board

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board_info)

        // set view
        tb = findViewById(R.id.tb_board_info_activity)
        tvName = findViewById(R.id.tv_board_info_name)
        tvDescription = findViewById(R.id.tv_board_info_description)
        tvTotalMembers = findViewById(R.id.tv_board_info_total_members)
        tvStatusContinue = findViewById(R.id.tv_board_info_status_continue)
        tvStatusFinished = findViewById(R.id.tv_board_info_status_finished)
        civImage = findViewById(R.id.civ_board_info_img)

        // get board
        board = intent.getParcelableExtra(Utils.SEND_BOARD)!!

        // set toolbar
        setToolBar()

        // set ui
        setUI()

    }

    private fun setToolBar() {
        setSupportActionBar(tb)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Infomation"
        tb.setNavigationIcon(R.drawable.ic_back_indicator_text_primary)
        tb.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun setUI() {
        tvName.text = board.name
        tvDescription.text = board.description
        tvTotalMembers.text = board.assignedUsers!!.size.toString()
        Utils.setImageUrl(this, civImage, board.image, R.drawable.img_work)
        if(board.finished!!) {
            tvStatusFinished.visibility = View.VISIBLE
            tvStatusContinue.visibility = View.GONE
        } else {
            tvStatusFinished.visibility = View.GONE
            tvStatusContinue.visibility = View.VISIBLE
        }
    }
}