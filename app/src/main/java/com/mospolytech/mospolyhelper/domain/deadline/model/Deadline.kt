package com.mospolytech.mospolyhelper.domain.deadline.model


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
    var importance: Int = R.color.colorLow) {


//    override fun hashCode(): Int {
//        return id.hashCode()
//    }
//
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
//        //if (pinned != other.pinned) return false
//        if (date != other.date) return false
//        if (time != other.time) return false
//        if (importance != other.importance) return false
//
//        return true
//    }

}

