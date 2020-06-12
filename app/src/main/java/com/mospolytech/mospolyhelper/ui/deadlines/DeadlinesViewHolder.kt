package com.mospolytech.mospolyhelper.ui.deadlines

import android.view.ContextMenu
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.repository.models.Deadline
import kotlinx.android.synthetic.main.item_deadline.view.*
import java.util.*

class DeadlinesViewHolder(view : View) : RecyclerView.ViewHolder(view) {
    val predmet = view.tvPred
    val zadanie = view.tvZad
    val completed = view.rbComp
    val pinned = view.imgPin
    val datetime = view.tvDateTime
    //val importance : Int? = R.color.colorLow
    val card = view.card
    val img = view.imgClock
    private var contextMenu: ContextMenu? = null

    private lateinit var deadline: Deadline

    fun setDeadline(deadline: Deadline, color: Int,
                    onCreateContextMenuListener: View.OnCreateContextMenuListener,
                    clickListener: View.OnClickListener) {
        this.deadline = deadline
        predmet.text = deadline.name.toUpperCase(Locale.ROOT)
        zadanie.text = deadline.description
        completed.isChecked = deadline.completed
        if (deadline.pinned){
            pinned.visibility = View.VISIBLE
        }
        else  { pinned.visibility = View.INVISIBLE }
        if (deadline.date == "") {
             datetime.text = if (deadline.time == "") "" else deadline.time
        } else {
             datetime.text = if (deadline.time == "") deadline.date else "${deadline.date}, ${deadline.time}"
        }
        when (deadline.importance) {
            R.color.colorLow -> {
                img.setImageResource(R.drawable.ic_access_alarm_low_24dp)
            }
            R.color.colorMedium -> {
                img.setImageResource(R.drawable.ic_access_alarm_medium_24dp)
            }
            R.color.colorHigh -> {
                img.setImageResource(R.drawable.ic_access_alarm_high_24dp)
            }
        }
        predmet.visibility = if (predmet.text.isEmpty()) View.GONE else View.VISIBLE
        completed.setOnClickListener(clickListener)
        card.setCardBackgroundColor(color)
        card.setOnCreateContextMenuListener(onCreateContextMenuListener)
    }

    fun getDeadline(): Deadline {
        return deadline
    }

    fun setcontextMenu(c: ContextMenu) {
        contextMenu = c
    }

    fun closeContextMenu() {
        contextMenu?.close()
    }
}