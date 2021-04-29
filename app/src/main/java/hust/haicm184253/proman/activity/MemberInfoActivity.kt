package hust.haicm184253.proman.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import de.hdodenhof.circleimageview.CircleImageView
import hust.haicm184253.proman.R
import hust.haicm184253.proman.model.Board
import hust.haicm184253.proman.model.User
import hust.haicm184253.proman.utils.Utils

class MemberInfoActivity : AppCompatActivity() {
    lateinit var tb: Toolbar
    lateinit var tvName: TextView
    lateinit var tvEmail: TextView
    lateinit var tvPhoneNumber: TextView
    lateinit var civImage: CircleImageView

    lateinit var member: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_member_info)

        // set view
        tb = findViewById(R.id.tb_member_info_activity)
        tvName = findViewById(R.id.tv_member_info_name)
        tvEmail = findViewById(R.id.tv_member_info_email)
        tvPhoneNumber = findViewById(R.id.tv_member_info_phone)
        civImage = findViewById(R.id.civ_member_info_img)

        // get member
        member = intent.getParcelableExtra(Utils.SEND_USER)!!

        // set toolbar
        setToolBar()

        // set ui
        setUI()

    }

    private fun setToolBar() {
        setSupportActionBar(tb)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = member.name
        tb.setNavigationIcon(R.drawable.ic_back_indicator_text_primary)
        tb.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun setUI() {
        tvName.text = member.name
        tvEmail.text = member.email
        tvPhoneNumber.text = member.phoneNumber
        Utils.setImageUrl(this, civImage, member.image, R.drawable.img_profile)
    }
}