package hust.haicm184253.proman

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView

class LauncherActivity : AppCompatActivity() {

    lateinit var tvProjectManager: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)

        tvProjectManager = findViewById(R.id.tv_project_manager)

        // set font
        tvProjectManager.typeface = Typeface.createFromAsset(assets, "WinterYesterday.ttf")

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, IntroActivity::class.java))
            finish()
        }, 2500)


    }
}