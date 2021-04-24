package hust.haicm184253.proman.activity

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import hust.haicm184253.proman.R

open class BaseActivity : AppCompatActivity() {

    var clickedOnce = false
    lateinit var waitingDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun showWaitingDialog() {
        waitingDialog = Dialog(this)
        waitingDialog.setContentView(R.layout.dialog_waiting)
        waitingDialog.show()
    }

    fun dismissWaitingDialog() {
        waitingDialog.dismiss()
    }

    fun showToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }

    fun doubleClickToExit() {
        if(clickedOnce) {
            return onBackPressed()
        }
        clickedOnce = true
        showToast("Click again to exit.")
        Handler(Looper.getMainLooper()).postDelayed({
            clickedOnce = false
        }, 3000)
    }

    fun showSnackBar(text: String, color: Int) {
        val snackbar = Snackbar.make(findViewById(android.R.id.content), text, Snackbar.LENGTH_LONG)
        snackbar.view.setBackgroundColor(ContextCompat.getColor(this, color))
        snackbar.show()
    }

}