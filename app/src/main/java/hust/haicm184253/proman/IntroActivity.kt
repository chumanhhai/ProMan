package hust.haicm184253.proman

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

class IntroActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var btnSignUpIntro: Button
    lateinit var btnSignInIntro: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        // set view
        btnSignUpIntro = findViewById(R.id.btn_sign_up_intro)
        btnSignInIntro = findViewById(R.id.btn_sign_in_intro)

        // set on click
        btnSignUpIntro.setOnClickListener(this)
        btnSignInIntro.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when(v!!.id) {
            R.id.btn_sign_up_intro -> {
                startActivity(Intent(this, SignUpActivity::class.java))
            }
            R.id.btn_sign_in_intro -> {
                startActivity(Intent(this, SignInActivity::class.java))
            }
        }
    }
}