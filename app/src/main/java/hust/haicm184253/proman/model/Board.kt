package hust.haicm184253.proman.model

import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.RequiresApi

data class Board(
    var uid: String? = null,
    var name: String? = null,
    var description: String? = null,
    var image: String? = null,
    var finished: Boolean? = false,
    var createdBy: String? = null,
    var createdAt: Long? = null,
    var assignedUsers: ArrayList<String>? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        parcel.readString(),
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.createStringArrayList(),
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(uid)
        parcel.writeString(name)
        parcel.writeString(description)
        parcel.writeString(image)
        parcel.writeValue(finished)
        parcel.writeString(createdBy)
        parcel.writeValue(createdAt)
        parcel.writeStringList(assignedUsers)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Board> {
        override fun createFromParcel(parcel: Parcel): Board {
            return Board(parcel)
        }

        override fun newArray(size: Int): Array<Board?> {
            return arrayOfNulls(size)
        }
    }
}
