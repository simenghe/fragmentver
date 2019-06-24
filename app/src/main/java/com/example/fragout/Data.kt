package com.example.fragout

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class Data (
    @SerializedName("a")
    var a:String? = null,
    @SerializedName("endingTime")
    var endingTime:String? = null,
    @SerializedName("floor")
    var floor:Int? = null,
    @SerializedName("meetingRoom")
    var meetingRoom:String?= null,
    @SerializedName("meetingState")
    var meetingState:String? = null,
    @SerializedName("startingTime")
    var startingTime:String? = null,
    @SerializedName("title")
    var title:String? = null,
    @SerializedName("userName")
    var userName:String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(a)
        parcel.writeString(endingTime)
        parcel.writeValue(floor)
        parcel.writeString(meetingRoom)
        parcel.writeString(meetingState)
        parcel.writeString(startingTime)
        parcel.writeString(title)
        parcel.writeString(userName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Data> {
        override fun createFromParcel(parcel: Parcel): Data {
            return Data(parcel)
        }

        override fun newArray(size: Int): Array<Data?> {
            return arrayOfNulls(size)
        }
    }
}