package hust.haicm184253.proman.model

import android.os.Parcel
import android.os.Parcelable


data class User(
        var uid: String? = null,
        val email: String? = null,
        val password: String? = null,
        var name: String? = null,
        var image: String? = null,
        var phoneNumber: String? = null
) : Parcelable {
        constructor(parcel: Parcel) : this(
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString()
        ) {
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
                parcel.writeString(uid)
                parcel.writeString(email)
                parcel.writeString(password)
                parcel.writeString(name)
                parcel.writeString(image)
                parcel.writeString(phoneNumber)
        }

        override fun describeContents(): Int {
                return 0
        }

        companion object CREATOR : Parcelable.Creator<User> {
                override fun createFromParcel(parcel: Parcel): User {
                        return User(parcel)
                }

                override fun newArray(size: Int): Array<User?> {
                        return arrayOfNulls(size)
                }
        }
}