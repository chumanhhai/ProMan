package hust.haicm184253.proman.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.widget.Toolbar
import de.hdodenhof.circleimageview.CircleImageView
import hust.haicm184253.proman.R
import hust.haicm184253.proman.model.Board
import hust.haicm184253.proman.model.User
import hust.haicm184253.proman.network.BoardAPI
import hust.haicm184253.proman.network.StorageAPI
import hust.haicm184253.proman.utils.Utils
import java.util.*
import kotlin.collections.HashMap

class CreateBoardActivity : BaseActivity(), View.OnClickListener {

    lateinit var tbCreateBoard: Toolbar
    lateinit var civBoard: CircleImageView
    lateinit var etBoardName: EditText
    lateinit var etDescription: EditText
    lateinit var btnCreateBoard: Button

    var localImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_board)

        // set view
        tbCreateBoard = findViewById(R.id.tb_create_board)
        civBoard = findViewById(R.id.civ_create_board_img)
        etBoardName = findViewById(R.id.et_board_name)
        btnCreateBoard = findViewById(R.id.btn_create_board)
        etDescription = findViewById(R.id.et_board_description)

        // set tool bar
        setToolBar()

        // set on click
        civBoard.setOnClickListener(this)
        btnCreateBoard.setOnClickListener(this)
    }

    fun setToolBar() {
        setSupportActionBar(tbCreateBoard)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_back_indicator_text_primary)
        tbCreateBoard.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onClick(v: View?) {
        when(v!!.id) {
            R.id.btn_create_board -> {
                val boardName = etBoardName.text.toString()
                val description = etDescription.text.toString()
                // close soft keyboard
                Utils.closeKeyboard(this)
                when {
                    boardName.isEmpty() -> {
                        showSnackBar("Board name must not be empty.", R.color.colorRedWarning)
                    }
                    localImageUri == null -> {
                        showSnackBar("Board image is unselected.", R.color.colorRedWarning)
                    }
                    description.isEmpty() -> {
                        showSnackBar("Board description must not be empty.", R.color.colorRedWarning)
                    }
                    else -> {
                        // show waiting dialog
                        showWaitingDialog()

                        // add board
                        val id = UUID.randomUUID().toString()
                        val currentTime = Date().time
                        val imageName = Utils.getBoardImageName(currentTime.toString())
                        val currentUserId = Utils.getCurrentUserUid()
                        val assignedUsers = arrayListOf(currentUserId)

                        StorageAPI.storeImage(imageName, localImageUri!!, { url: String ->
                            val board = Board(id, boardName, description, url, false, currentUserId, currentTime, assignedUsers)
                            BoardAPI.addBoard(board, {
                                // send data back
                                val intent = Intent()
                                intent.putExtra(Utils.SEND_BOARD, board)
                                setResult(Activity.RESULT_OK, intent)

                                // ui
                                dismissWaitingDialog()
                                showToast("Board is created successfully.")
                                finish()
                            }, {
                                showToast(resources.getString(R.string.went_wrong))
                                dismissWaitingDialog()
                            })
                        }, {
                            showToast(resources.getString(R.string.went_wrong))
                            dismissWaitingDialog()
                        })
                    }
                }
            }
            R.id.civ_create_board_img -> {
                Utils.chooseImageFromGallery(this)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK) {
            when(requestCode) {
                Utils.CHOOSE_IMAGE_FROM_GALLERY_REQUEST_CODE -> {
                    data?.let {
                        localImageUri = data.data
                        Utils.setImageUrl(this, civBoard, localImageUri.toString(), R.drawable.img_work)
                    }
                }
            }
        }
    }

}