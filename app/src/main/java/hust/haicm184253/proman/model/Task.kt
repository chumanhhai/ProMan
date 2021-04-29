package hust.haicm184253.proman.model

import android.os.Parcel
import android.os.Parcelable

data class Task(
        val uid: String? = null,
        val name: String? = null,
        val description: String? = null,
        val createdBy: String? = null,
        val percent: Int? = null,
        val assignedUsers: ArrayList<String>? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.createStringArrayList()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(uid)
        parcel.writeString(name)
        parcel.writeString(description)
        parcel.writeString(createdBy)
        parcel.writeValue(percent)
        parcel.writeStringList(assignedUsers)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Task> {
        override fun createFromParcel(parcel: Parcel): Task {
            return Task(parcel)
        }

        override fun newArray(size: Int): Array<Task?> {
            return arrayOfNulls(size)
        }
    }
}


