package hust.haicm184253.proman.network

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import hust.haicm184253.proman.model.User
import hust.haicm184253.proman.utils.Utils
import hust.haicm184253.proman.utils.Utils.Companion.COLLECTION_USER
import hust.haicm184253.proman.utils.Utils.Companion.getCurrentUserUid

class UserAPI {
    companion object {

        fun register(email: String, password: String,
            successCallback: () -> Unit,
            failureCallback: () -> Unit) {
            val auth = Firebase.auth

            auth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener { auth ->
                        successCallback()
                    }
                    .addOnFailureListener {
                        failureCallback()
                    }
        }

        fun addUser(user: HashMap<String, Any>,
                     successCallback: () -> Unit,
                     failureCallback: () -> Unit) {
            val fireStore = FirebaseFirestore.getInstance()
            fireStore.collection(COLLECTION_USER)
                .document(user[Utils.USER_ATTR_ID] as String)
                .set(user)
                .addOnSuccessListener {
                    successCallback()
                }
                .addOnFailureListener {
                    failureCallback()
                }
        }

        fun getUserByEmail(email: String,
            successCallback: (user: User?) -> Unit,
            failureCallback: () -> Unit) {
            val db = Firebase.firestore

            db.collection(Utils.COLLECTION_USER)
                .whereEqualTo(Utils.USER_ATTR_EMAIL, email)
                .limit(1)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    if(documentSnapshot.isEmpty)
                        successCallback(null)
                    else
                        successCallback(documentSnapshot.toObjects(User::class.java).get(0))
                }
                .addOnFailureListener {
                    failureCallback()
                }
        }

        fun getCurrentUser(successCallback: (user: User) -> Unit,
                        failureCallback: () -> Unit) {
            val uid = getCurrentUserUid()
            val firestore = FirebaseFirestore.getInstance()

            firestore.collection(COLLECTION_USER)
                .document(uid)
                .get()
                .addOnCompleteListener { task ->
                    if(task.isSuccessful) {
                        successCallback(task.result!!.toObject(User::class.java)!!)
                    } else {
                        failureCallback()
                    }
                }
        }

        fun getUserById(uid: String,
            successCallback: (user: User) -> Unit,
            failureCallback: () -> Unit) {
            val db = FirebaseFirestore.getInstance()

            db.collection(Utils.COLLECTION_USER)
                    .document(uid)
                    .get()
                    .addOnSuccessListener { documentSnapShot ->
                        successCallback(documentSnapShot.toObject(User::class.java)!!)
                    }
                    .addOnFailureListener {
                        failureCallback()
                    }
        }

        fun updateCurrentUser(update: HashMap<String, Any?>,
                              successCallback: () -> Unit,
                              failureCallback: () -> Unit) {
            val db = FirebaseFirestore.getInstance()

            db.collection(COLLECTION_USER)
                .document(getCurrentUserUid())
                .update(update)
                .addOnSuccessListener {
                    successCallback()
                }
                .addOnFailureListener {
                    failureCallback()
                }
        }

        fun getAllMembersOfBoard(idsList: ArrayList<String>,
            successCallback: (users: ArrayList<User>) -> Unit,
            failureCallback: () -> Unit) {
            val db = Firebase.firestore

            db.collection(Utils.COLLECTION_USER)
                .whereIn(Utils.USER_ATTR_ID, idsList)
                .get()
                .addOnSuccessListener { documentSnapshots ->
                    val users = ArrayList<User>()
                    for(documentSnapshot in documentSnapshots.documents) {
                        val user = documentSnapshot.toObject(User::class.java)!!
                        users.add(user)
                    }
                    successCallback(users)
                }
                .addOnFailureListener {
                    failureCallback()
                }
        }
    }
}