package com.mospolytech.mospolyhelper.features.ui.deadlines


import android.content.Context
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.databinding.ItemDeadlineBinding
import com.mospolytech.mospolyhelper.domain.deadline.model.Deadline
import com.mospolytech.mospolyhelper.features.utils.getAttributeRes
import com.mospolytech.mospolyhelper.utils.DeadlinesDiffCallback
import java.util.*


class MyDeadlineRecyclerViewAdapter(
    private var items : List<Deadline>,
    private val deadlineViewModel : DeadlineViewModel
) : RecyclerView.Adapter<MyDeadlineRecyclerViewAdapter.DeadlinesViewHolder>() {

    lateinit var context: Context

    fun updateBookList(newDeadlinesList: List<Deadline>) {
        val diffResult2 =
            DiffUtil.calculateDiff(DeadlinesDiffCallback(items, newDeadlinesList), false)
        items = newDeadlinesList
        diffResult2.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeadlinesViewHolder {
        context = parent.context
        return DeadlinesViewHolder(LayoutInflater.from(context)
            .inflate(R.layout.item_deadline, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: DeadlinesViewHolder, position: Int) {
        val color: Int = when {
            items[position].pinned -> R.color.colorCompleted
            items[position].completed -> R.color.colorPinned
            else -> context.getAttributeRes(R.attr.colorSurface)!!
        }
        val listener = View.OnCreateContextMenuListener { menu, _, _ ->
            if (items[position].pinned) {
                menu.add("Открепить").setOnMenuItemClickListener {
                    deadlineViewModel.setPinned(items[position])
                    true
                }
            } else {
                menu.add("Закрепить").setOnMenuItemClickListener {
                    deadlineViewModel.setPinned(items[position])
                    true
                }
            }
            if (!items[position].completed) {
                menu.add("Выполнено").setOnMenuItemClickListener {
                    deadlineViewModel.setCompleted(items[position])
                    true
                }
                menu.add("Редактировать").setOnMenuItemClickListener {
                    deadlineViewModel.edit(items[position])
                    true
                }
            } else {
                menu.add("Отменить выполнение").setOnMenuItemClickListener {
                    deadlineViewModel.setCompleted(items[position])
                    true
                }
            }
            menu.add("Удалить").setOnMenuItemClickListener {
                deadlineViewModel.delete(items[position])
                true
            }
            holder.setcontextMenu(menu)
        }
        val click = View.OnClickListener {
            deadlineViewModel.setCompleted(items[position])
        }
        holder.setDeadline(items[position],
            ContextCompat.getColor(context, color), listener, click)
    }

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
}

