package hust.haicm184253.proman.utils

import android.Manifest
import android.content.ContentResolver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.webkit.MimeTypeMap
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Placeholder
import androidx.core.net.toFile
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import hust.haicm184253.proman.R
import hust.haicm184253.proman.model.User
import java.io.File
import java.net.URI
import java.util.*
import kotlin.collections.HashMap


class Utils {
    companion object {

        // collection name
        val COLLECTION_USER = "user"
        val COLLECTION_BOARD = "board"
        val COLLECTION_TASK = "task"

        // user attributes
        val USER_ATTR_ID = "uid"
        val USER_ATTR_NAME = "name"
        val USER_ATTR_EMAIL = "email"
        val USER_ATTR_IMAGE = "image"
        val USER_ATTR_PHONE_NUM = "phoneNumber"

        // board attributes
        val BOARD_ATTR_ID = "uid"
        val BOARD_ATTR_NAME = "name"
        val BOARD_ATTR_DESCRIPTION = "description"
        val BOARD_ATTR_IMAGE = "image"
        val BOARD_ATTR_FINISHED = "finished"
        val BOARD_ATTR_CREATED_BY = "createdBy"
        val BOARD_ATTR_CREATED_AT = "createdAt"
        val BOARD_ATTR_ASSIGNED_USERS = "assignedUsers"

        // task attributes
        val TASK_ATTR_ID = "uid"
        val TASK_ATTR_NAME = "name"
        val TASK_ATTR_DESCRIPTION = "description"
        val TASK_ATTR_PERCENT = "percent"
        val TASK_ATTR_ASSIGNED_USERS = "assignedUsers"
        val TASK_ATTR_CREATED_BY = "createdBy"

        // code for passing data in intent
        val SEND_USER = "user info"
        val SEND_BOARD = "board info"
        val SEND_MEMBER = "members info"
        val SEND_HOST = "host info"
        val SEND_ID = "id info"
        val SEND_TASK = "task info"
        val SEND_OBJECT_INDEX = "index info"

        // request code for start activity for result
        val CHOOSE_IMAGE_FROM_GALLERY_REQUEST_CODE = 0
        val GET_USER_REQUEST_CODE = 1
        val GET_BOARD_REQUEST_CODE = 2
        val MEMBER_CHANGE_REQUEST_CODE = 3
        val BOARD_CHANGE_REQUEST_CODE = 4
        val GET_TASK_REQUEST_CODE = 5
        val GET_TASK_FOR_UPDATE_REQUEST_CODE = 6

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

        fun setImageUrl(context: Context, view: ImageView, url: String?, placeholder: Int) {
            Glide.with(context)
                .load(url)
                .placeholder(placeholder)
                .into(view);
        }

        fun getCurrentUserUid(): String {
            return FirebaseAuth.getInstance().currentUser!!.uid
        }

        fun getUserImageName(): String {
            return "USER_IMAGE_" + getCurrentUserUid()
        }

        fun getBoardImageName(createdAt: String): String {
            return "BOARD_IMAGE_" + createdAt
        }

        fun chooseImageFromGallery(context: AppCompatActivity) {
            Dexter.withContext(context)
                    .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    .withListener(object : PermissionListener {
                        override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                            context.startActivityForResult(intent, Utils.CHOOSE_IMAGE_FROM_GALLERY_REQUEST_CODE)
                        }
                        override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                            AlertDialog.Builder(context)
                                    .setTitle("Permission need to be granted to use this feature.")
                                    .setPositiveButton("Go to setting", DialogInterface.OnClickListener { dialog, which ->
                                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                                Uri.fromParts("package", context.packageName, null))
                                        context.startActivity(intent)
                                    })
                                    .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which ->
                                        dialog.dismiss()
                                    })
                                    .show()
                        }
                        override fun onPermissionRationaleShouldBeShown(p0: PermissionRequest?, p1: PermissionToken?) {
                            p1!!.continuePermissionRequest()
                        }
                    })
                    .onSameThread()
                    .check()
        }

        fun getEmailsFromUsers(users: ArrayList<User>) : ArrayList<String> {
            val userEmails = ArrayList<String>()

            for(user in users)
                userEmails.add(user.email!!)

            return userEmails
        }

        fun fromEmailsToIdsUsers(users: ArrayList<User>) : HashMap<String, String> {
            val hashMap = HashMap<String, String>()
            for(user in users)
                hashMap[user.email!!] = user.uid!!

            return hashMap
        }
    }
}