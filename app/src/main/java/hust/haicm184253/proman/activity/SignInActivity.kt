package hust.haicm184253.proman.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import hust.haicm184253.proman.R
import hust.haicm184253.proman.utils.Utils

class SignInActivity : BaseActivity() {
    lateinit var tbSignIn: Toolbar
    lateinit var etEmail: EditText
    lateinit var etPassword: EditText
    lateinit var btnSignIn: Button

    var auth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        // set action bar
        tbSignIn = findViewById(R.id.tb_sign_in)
        setSupportActionBar(tbSignIn)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_back_indicator)
        supportActionBar!!.setTitle("")
        tbSignIn.setNavigationOnClickListener {
            onBackPressed()
        }

        // set view
        etEmail = findViewById(R.id.et_sign_in_email)
        etPassword = findViewById(R.id.et_sign_in_password)
        btnSignIn = findViewById(R.id.btn_sign_in_sign_in)

        // set on click
        btnSignIn.setOnClickListener {
            logIn()
            Utils.closeKeyboard(this)
        }
    }

    private fun logIn() {
        val email = etEmail.text.toString()
        val password = etPassword.text.toString()

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
            else -> {
                showWaitingDialog()
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if(task.isSuccessful) {
                            startActivity(Intent(this, MainActivity::class.java))
                        } else {
                            showToast("Login Failed!")
                        }
                        dismissWaitingDialog()
                    }
            }
        }
    }
}