package com.mospolytech.mospolyhelper.repository.models


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
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Deadline) return false
        //val deadline = other as? Deadline
        return false
        /*id == deadline?.id
        && this.name == deadline?.name
        && this.description == deadline.description
        && this.completed == deadline.completed
        && this.pinned == deadline.pinned*/
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

}

