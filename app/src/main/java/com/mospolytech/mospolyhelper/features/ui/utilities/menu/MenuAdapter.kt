package com.mospolytech.mospolyhelper.features.ui.utilities.menu

import android.view.*
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.databinding.ItemMenuBinding
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
        private val viewBinding by viewBinding(ItemMenuBinding::bind)

        fun bind(menuItem: MenuItem) {
            with(viewBinding) {
                container.setOnClickListener {
                    (onItemMenuClick as Action1).invoke(menuItem.itemId)
                }
                imageIcon.setImageDrawable(menuItem.icon)
                textLabel.text = menuItem.title
            }
        }
    }
}