package com.mospolytech.mospolyhelper.features.ui.deadlines

import android.view.ContextMenu
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.databinding.ItemDeadlineBinding
import com.mospolytech.mospolyhelper.domain.deadline.model.Deadline
import java.util.*

class DeadlinesViewHolder(view : View) : RecyclerView.ViewHolder(view) {

    private val viewBinding by viewBinding(ItemDeadlineBinding::bind)

    private val predmet = viewBinding.tvPred
    private val zadanie = viewBinding.tvZad
    private val completed = viewBinding.rbComp
    private val pinned = viewBinding.imgPin
    private val datetime = viewBinding.tvDateTime
    //val importance : Int? = R.color.colorLow
    private val card = viewBinding.card
    private val img = viewBinding.imgClock
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
                img.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_access_alarm_low_24dp,0, 0, 0)
            }
            R.color.colorMedium -> {
                img.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_access_alarm_medium_24dp,0, 0, 0)
            }
            R.color.colorHigh -> {
                img.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_access_alarm_high_24dp,0, 0, 0)
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