package hust.haicm184253.proman

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.widget.Toolbar

class SignInActivity : AppCompatActivity() {
    lateinit var tbSignIn: Toolbar
    lateinit var etEmail: EditText
    lateinit var etPassword: EditText

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
    }
}