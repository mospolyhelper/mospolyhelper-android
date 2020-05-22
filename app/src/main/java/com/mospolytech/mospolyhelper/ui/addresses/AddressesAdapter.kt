package com.mospolytech.mospolyhelper.ui.addresses

import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.repository.model.Addresses

class AddressesAdapter(
    var buildings: Addresses
) : RecyclerView.Adapter<AddressesAdapter.ViewHolder>() {
    override fun getItemCount() = buildings.size

    fun update(buildings: Addresses ) {
        this.buildings = buildings
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_address, parent, false);
        return ViewHolder(view);
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        var spanned = HtmlCompat.fromHtml(buildings["addresses"]!![position], HtmlCompat.FROM_HTML_MODE_LEGACY);
        viewHolder.text.setText(spanned, TextView.BufferType.NORMAL);
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val text: TextView = view.findViewById<TextView>(R.id.text);
    }

    class ItemDecoration(val offset: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            outRect.top = offset;
        }
    }
}