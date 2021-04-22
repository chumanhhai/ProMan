package hust.haicm184253.proman

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.widget.Toolbar

class SignUpActivity : AppCompatActivity() {

    lateinit var tbSignUp: Toolbar
    lateinit var etEmail: EditText
    lateinit var etPassword: EditText
    lateinit var etName: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // set action bar
        tbSignUp = findViewById(R.id.tb_sign_up)
        setSupportActionBar(tbSignUp)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_back_indicator)
        supportActionBar!!.setTitle("")
        tbSignUp.setNavigationOnClickListener {
            onBackPressed()
        }

        // set view
        etEmail = findViewById(R.id.et_sign_up_email)
        etPassword = findViewById(R.id.et_sign_up_password)
        etName = findViewById(R.id.et_sign_up_name)
    }


}