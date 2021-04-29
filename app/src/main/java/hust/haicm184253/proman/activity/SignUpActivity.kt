package hust.haicm184253.proman.activity

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import hust.haicm184253.proman.R
import hust.haicm184253.proman.network.UserAPI
import hust.haicm184253.proman.utils.Utils
import java.util.*

class SignUpActivity : BaseActivity() {

    lateinit var tbSignUp: Toolbar
    lateinit var etEmail: EditText
    lateinit var etPassword: EditText
    lateinit var etName: EditText
    lateinit var btnSignUp: Button

    var auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // set action bar
        tbSignUp = findViewById(R.id.tb_sign_up)
        setSupportActionBar(tbSignUp)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_back_indicator_text_primary)
        supportActionBar!!.setTitle("")
        tbSignUp.setNavigationOnClickListener {
            onBackPressed()
        }

        // set view
        etEmail = findViewById(R.id.et_sign_up_email)
        etPassword = findViewById(R.id.et_sign_up_password)
        etName = findViewById(R.id.et_sign_up_name)
        btnSignUp = findViewById(R.id.btn_sign_up_sign_up)

        btnSignUp.setOnClickListener {
            register()
            Utils.closeKeyboard(this)
        }
    }

    private fun register() {
        val email = etEmail.text.toString()
        val password = etPassword.text.toString()
        val name = etName.text.toString()

        when {
            email.isNullOrEmpty() -> {
                showSnackBar("Email must not be empty.", R.color.colorRedWarning)
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                showSnackBar("Invalid email.", R.color.colorRedWarning)
            }
            password.length < 6 -> {
                showSnackBar("Password length must be > 5", R.color.colorRedWarning)
            }
            name.trim().isNullOrEmpty() -> {
                showSnackBar("Name must not be empty.", R.color.colorRedWarning)
            }
            else -> {
                showWaitingDialog()

                val successCallBack: () -> Unit = {
                    showToast("Welcome ${name}!")
                    dismissWaitingDialog()
                    finish()
                }
                val failureCallBack: () -> Unit = {
                    showToast("Register Failed!")
                    dismissWaitingDialog()
                }

                UserAPI.register(email, password,{
                    // store user in user collection
                    val id = Utils.getCurrentUserUid()
                    val user = hashMapOf<String, Any>(
                            Utils.USER_ATTR_ID to id,
                            Utils.USER_ATTR_EMAIL to email,
                            Utils.USER_ATTR_NAME to name,
                            Utils.USER_ATTR_IMAGE to "",
                            Utils.USER_ATTR_PHONE_NUM to "")
                    UserAPI.addUser(user, successCallBack, failureCallBack)
                }, failureCallBack)
            }
        }

    }


}