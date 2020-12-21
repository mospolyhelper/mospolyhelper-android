package com.mospolytech.mospolyhelper.features.ui.schedule

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.mospolytech.mospolyhelper.R
import java.text.DateFormatSymbols
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class DateAdapter(
    val dateFrom: LocalDate,
    val dateTo: LocalDate
) : RecyclerView.Adapter<DateAdapter.ViewHolder>() {
    companion object {
        private val dateFormatter = DateTimeFormatter.ofPattern("d\nEE")
    }

    val firstDate = dateFrom.minusDays((dateFrom.dayOfWeek.value - DayOfWeek.MONDAY.value).toLong())
    val lastDate = dateTo.plusDays((DayOfWeek.SUNDAY.value - dateTo.dayOfWeek.value).toLong())
    private val count = ((firstDate.until(lastDate, ChronoUnit.DAYS) + 1) / 7).toInt()

    override fun getItemCount() = count

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.page_schedule_date, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        (holder as ViewHolder).bind()
    }

    fun getPositionByDate(date: LocalDate): Int {
        val monday = date.minusDays((date.dayOfWeek.value - DayOfWeek.MONDAY.value).toLong())
        return (firstDate.until(monday, ChronoUnit.DAYS) / 7).toInt()
    }


    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val tabLayout = view.findViewById<TabLayout>(R.id.tablayout_schedule)
        fun bind() {
            val firstDateOfWeek = firstDate.plusWeeks(adapterPosition.toLong())
            for (i in 0..6) {
                tabLayout.getTabAt(i)?.text =
                    firstDateOfWeek.plusDays(i.toLong()).format(dateFormatter).toUpperCase()
            }
        }
    }
}