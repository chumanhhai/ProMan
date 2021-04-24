package hust.haicm184253.proman.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity


class Utils {
    companion object {
        fun closeKeyboard(context: AppCompatActivity) {
            // this will give us the view
            // which is currently focus
            // in this layout
            val view: View? = context.currentFocus

            // if nothing is currently
            // focus then this will protect
            // the app from crash
            if (view != null) {

                // now assign the system
                // service to InputMethodManager
                val manager: InputMethodManager? = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                manager?.hideSoftInputFromWindow(
                        view.getWindowToken(), 0
                    )
            }
        }
    }
}