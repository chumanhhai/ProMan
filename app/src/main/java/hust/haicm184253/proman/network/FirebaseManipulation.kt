package hust.haicm184253.proman.network

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import hust.haicm184253.proman.activity.SignUpActivity
import hust.haicm184253.proman.model.User

class FirebaseManipulation {
    companion object {

        val COLLECTION_USER = "user"

        fun register(context: SignUpActivity, user: User) {
            val fireStore = FirebaseFirestore.getInstance()
            fireStore.collection(COLLECTION_USER)
                    .document(getCurrentUserUid())
                    .set(user)
                    .addOnCompleteListener { task ->
                        if(task.isSuccessful) {
                            context.showToast("Welcome ${user.name}!")
                        } else {
                            context.showToast("Register Failed!")
                        }
                        context.dismissWaitingDialog()
                        context.finish()
                    }
        }

        fun getCurrentUserUid(): String {
            return FirebaseAuth.getInstance().currentUser!!.uid
        }
    }
}