package com.mospolytech.mospolyhelper.features.ui.addresses

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.domain.addresses.model.Addresses

class AddressesPageAdapter(
    private val addresses: Addresses
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.page_addresses, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind()
    }

    override fun getItemCount() = addresses.size

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_addresses)

        init {
            recyclerView.layoutManager = LinearLayoutManager(view.context)
            val scale = view.context.resources.displayMetrics.density
            recyclerView.addItemDecoration(AddressesAdapter.ItemDecoration((8 * scale + 0.5f).toInt()))
            recyclerView.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
                override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                    if (e.action == MotionEvent.ACTION_DOWN &&
                        rv.scrollState == RecyclerView.SCROLL_STATE_SETTLING
                    ) {
                        rv.stopScroll()
                    }
                    return false
                }

                override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) = Unit

                override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) = Unit
            })
        }

        fun bind() {

            val pair = addresses.entries.toList()[adapterPosition]

            recyclerView.adapter = AddressesAdapter(pair.value, pair.key)
            recyclerView.adapter?.notifyDataSetChanged()
        }
    }
}