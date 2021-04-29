package hust.haicm184253.proman.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.Toolbar
import de.hdodenhof.circleimageview.CircleImageView
import hust.haicm184253.proman.R
import hust.haicm184253.proman.model.User
import hust.haicm184253.proman.network.StorageAPI
import hust.haicm184253.proman.network.UserAPI
import hust.haicm184253.proman.utils.Utils

class MyProfileActivity : BaseActivity(), View.OnClickListener {

    lateinit var tb: Toolbar
    lateinit var civAvatar: CircleImageView
    lateinit var etEmail: AppCompatEditText
    lateinit var etName: AppCompatEditText
    lateinit var etPhone: AppCompatEditText
    lateinit var btnUpdate: Button

    lateinit var user: User
    var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)

        // set view
        tb = findViewById(R.id.tb_my_profile_activity)
        civAvatar = findViewById(R.id.civ_my_profile_img)
        etEmail = findViewById(R.id.et_my_profile_email)
        etName = findViewById(R.id.et_my_profile_name)
        etPhone = findViewById(R.id.et_my_profile_mobile_phone)
        btnUpdate = findViewById(R.id.btn_my_profile_update)

        // get user info
        user = intent.getParcelableExtra(Utils.SEND_USER)!!

        // set action bar
        setToolBar()

        // set info
        setInfo()

        // set on click
        btnUpdate.setOnClickListener(this)
        civAvatar.setOnClickListener(this)

    }

    private fun setToolBar() {
        setSupportActionBar(tb)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        tb.setNavigationIcon(R.drawable.ic_back_indicator_text_primary)
        tb.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun setInfo() {
        etEmail.setText(user.email)
        etName.setText(user.name)
        etPhone.setText(user.phoneNumber)
        Utils.setImageUrl(this, civAvatar, user.image, R.drawable.img_profile)
    }

    override fun onClick(v: View?) {
        when(v!!.id) {
            R.id.btn_my_profile_update -> {
                val name = etName.text.toString()
                val phoneNum = etPhone.text.toString()
                when {
                    name.isNullOrEmpty() -> {
                        showSnackBar("Name must not be empty.", R.color.colorRedWarning)
                    }
                    else -> {
                        val update = hashMapOf<String, Any?>(
                            Utils.USER_ATTR_EMAIL to user.email,
                            Utils.USER_ATTR_NAME to name,
                            Utils.USER_ATTR_PHONE_NUM to phoneNum,
                            Utils.USER_ATTR_IMAGE to user.image
                        )


                        fun successCallBack() {
                            user.name = name
                            user.phoneNumber = phoneNum
                            user.image = update[Utils.USER_ATTR_IMAGE] as String
                            val intent = Intent()
                            intent.putExtra(Utils.SEND_USER, user)
                            setResult(RESULT_OK, intent)

                            dismissWaitingDialog()
                            showToast("Update Successfully!")
                            finish()
                        }

                        fun failureCallBack() {
                            dismissWaitingDialog()
                            showToast(resources.getString(R.string.went_wrong))
                        }

                        showWaitingDialog()

                        if (imageUri != null) {
                            // update user
                            StorageAPI.storeImage(Utils.getUserImageName(), imageUri!!, { url ->
                                update[Utils.USER_ATTR_IMAGE] = url
                                UserAPI.updateCurrentUser(
                                    update,
                                    { successCallBack() }
                                ) { failureCallBack() }
                            }, { failureCallBack() })
                        } else {
                            UserAPI.updateCurrentUser(
                                update,
                                { successCallBack() }
                            ) { failureCallBack() }
                        }


                    }
                }
            }
            R.id.civ_my_profile_img -> {
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
                        imageUri = data.data!!
//                        civAvatar.setImageURI(imageUri)
                        Utils.setImageUrl(this, civAvatar, imageUri.toString(), R.drawable.img_profile)
                    }
                }
            }
        }
    }
}