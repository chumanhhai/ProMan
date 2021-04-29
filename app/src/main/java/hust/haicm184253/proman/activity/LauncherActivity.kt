package hust.haicm184253.proman.activity

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import hust.haicm184253.proman.R

class LauncherActivity : AppCompatActivity() {

    lateinit var tvProjectManager: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)

        // hide status bar
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN


        tvProjectManager = findViewById(R.id.tv_project_manager)

        // set font
        tvProjectManager.typeface = Typeface.createFromAsset(assets, "WinterYesterday.ttf")

        Handler(Looper.getMainLooper()).postDelayed({
            var intent: Intent
            if(FirebaseAuth.getInstance().currentUser == null)
                intent = Intent(this, IntroActivity::class.java)
            else
                intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 2500)

    }
}