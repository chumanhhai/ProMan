package hust.haicm184253.proman.network

import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import hust.haicm184253.proman.model.Board
import hust.haicm184253.proman.utils.Utils

class BoardAPI {
    companion object {

        fun addBoard(board: Board,
                     successCallback: () -> Unit,
                     failureCallback: () -> Unit) {
            val db = Firebase.firestore

            db.collection(Utils.COLLECTION_BOARD)
                .document(board.uid!!)
                .set(board)
                .addOnSuccessListener {
                    successCallback()
                }
                .addOnFailureListener {
                    failureCallback()
                }
        }

        fun getAllBoardsOfCurrentUser(successCallback: (boards: ArrayList<Board>) -> Unit,
            failureCallback: () -> Unit) {
            val db = Firebase.firestore
            val boards = ArrayList<Board>()

            db.collection(Utils.COLLECTION_BOARD)
                    .whereArrayContains(Utils.BOARD_ATTR_ASSIGNED_USERS, Utils.getCurrentUserUid())
                    .orderBy(Utils.BOARD_ATTR_CREATED_AT, Query.Direction.DESCENDING)
                    .get()
                    .addOnSuccessListener { documentSnapshots ->
                        for(documentSnapshot in documentSnapshots.documents) {
                            val board = documentSnapshot.toObject(Board::class.java)!!
                            boards.add(board)
                        }
                        successCallback(boards)
                    }
                    .addOnFailureListener {
                        failureCallback()
                    }
        }

        fun updateBoardById(id: String, update: HashMap<String, Any>,
            successCallback: () -> Unit,
            failureCallback: () -> Unit) {
            val db = Firebase.firestore

            db.collection(Utils.COLLECTION_BOARD)
                .document(id)
                .update(update)
                .addOnSuccessListener {
                    successCallback()
                }
                .addOnFailureListener {
                    failureCallback()
                }
        }

        fun deleteBoardById(id: String,
            successCallback: () -> Unit,
            failureCallback: () -> Unit) {
            val db = Firebase.firestore

            db.collection(Utils.COLLECTION_BOARD)
                    .document(id)
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