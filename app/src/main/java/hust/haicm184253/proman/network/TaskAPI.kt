package hust.haicm184253.proman.network

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import hust.haicm184253.proman.model.Task
import hust.haicm184253.proman.model.User
import hust.haicm184253.proman.utils.Utils

class TaskAPI {
    companion object {
        fun getAllTasksOfBoard(id: String,
                                 successCallback: (task: ArrayList<Task>) -> Unit,
                                 failureCallback: () -> Unit) {
            val db = Firebase.firestore

            db.collection(Utils.COLLECTION_TASK)
                .whereEqualTo(Utils.TASK_ATTR_CREATED_BY, id)
                .get()
                .addOnSuccessListener { documentSnapshots ->
                    val tasks = ArrayList<Task>()
                    for(documentSnapshot in documentSnapshots.documents) {
                        val task = documentSnapshot.toObject(Task::class.java)!!
                        tasks.add(task)
                    }
                    successCallback(tasks)
                }
                .addOnFailureListener {
                    failureCallback()
                }
        }

        fun addTask(task: Task,
            successCallback: () -> Unit,
            failureCallback: () -> Unit) {
            val db = Firebase.firestore

            db.collection(Utils.COLLECTION_TASK)
                .document(task.uid!!)
                .set(task)
                .addOnSuccessListener {
                    successCallback()
                }
                .addOnFailureListener {
                    failureCallback()
                }
        }
    }
}