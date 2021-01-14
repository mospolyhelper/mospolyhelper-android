package com.mospolytech.mospolyhelper.domain.deadline.model


import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mospolytech.mospolyhelper.R

@Entity(tableName = "deadlines")
class Deadline(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    var name: String,
    var description: String? = null,
    var completed: Boolean = false,
    var pinned: Boolean = false,
    var date: String,
    var time:String,
    var importance: Int = R.color.colorLow): Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString()!!,
        parcel.readString(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(id)
        parcel.writeString(name)
        parcel.writeString(description)
        parcel.writeByte(if (completed) 1 else 0)
        parcel.writeByte(if (pinned) 1 else 0)
        parcel.writeString(date)
        parcel.writeString(time)
        parcel.writeInt(importance)
    }

    override fun describeContents(): Int {
        return 0
    }

//    override fun equals(other: Any?): Boolean {
//        if (this === other) return true
//        if (javaClass != other?.javaClass) return false
//
//        other as Deadline
//
//        if (id != other.id) return false
//        if (name != other.name) return false
//        if (description != other.description) return false
//        if (completed != other.completed) return false
//        if (pinned != other.pinned) return false
//        if (date != other.date) return false
//        if (time != other.time) return false
//        if (importance != other.importance) return false
//
//        return true
//    }
//
//    override fun hashCode(): Int {
//        var result = id ?: 0
//        result = 31 * result + name.hashCode()
//        result = 31 * result + (description?.hashCode() ?: 0)
//        result = 31 * result + completed.hashCode()
//        result = 31 * result + pinned.hashCode()
//        result = 31 * result + date.hashCode()
//        result = 31 * result + time.hashCode()
//        result = 31 * result + importance
//        return result
//    }

    companion object CREATOR : Parcelable.Creator<Deadline> {
        override fun createFromParcel(parcel: Parcel): Deadline {
            return Deadline(parcel)
        }

        override fun newArray(size: Int): Array<Deadline?> {
            return arrayOfNulls(size)
        }
    }

}

