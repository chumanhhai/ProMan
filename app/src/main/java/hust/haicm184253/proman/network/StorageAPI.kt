package hust.haicm184253.proman.network

import android.net.Uri
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class StorageAPI {
    companion object {
        fun storeImage(fileName: String,
            localImageUri: Uri,
            successCallback: (url: String) -> Unit,
            failureCallback: () -> Unit) {
            val reference = Firebase.storage.reference

            reference.child(fileName)
                .putFile(localImageUri)
                .addOnSuccessListener { taskSnapShot ->
                    taskSnapShot.metadata!!.reference!!.downloadUrl
                        .addOnSuccessListener { url ->
                            successCallback(url.toString())
                        }
                        .addOnFailureListener {
                            failureCallback()
                        }
                }
                .addOnFailureListener {
                    failureCallback()
                }
        }

        fun deleteImage(imageName: String,
                        successCallback: () -> Unit,
                        failureCallback: () -> Unit) {
            val reference = Firebase.storage.reference

            reference.child(imageName)
                .delete()
                .addOnSuccessListener {
                    successCallback()
                }
                .addOnFailureListener {
                    failureCallback()
                }
        }
    }
}