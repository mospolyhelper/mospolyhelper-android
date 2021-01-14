package com.mospolytech.mospolyhelper.features.ui.utilities.menu

import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.features.ui.utilities.addresses.AddressesAdapter
import com.mospolytech.mospolyhelper.utils.Action1
import com.mospolytech.mospolyhelper.utils.Event1

class MenuAdapter(
    private val menu: Menu
) : RecyclerView.Adapter<MenuAdapter.ViewHolder>() {

    val onItemMenuClick: Event1<Int> = Action1()

    override fun getItemCount() = menu.size()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_menu, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(menu[position])
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val icon: ImageView = view.findViewById(R.id.imageIcon)
        private val label: TextView = view.findViewById(R.id.textLabel)

        fun bind(menuItem: MenuItem) {
            itemView.setOnClickListener {
                (onItemMenuClick as Action1).invoke(menuItem.itemId)
            }
            icon.setImageDrawable(menuItem.icon)
            label.text = menuItem.title
        }
    }
}