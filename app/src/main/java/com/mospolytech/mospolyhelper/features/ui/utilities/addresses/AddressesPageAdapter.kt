package com.mospolytech.mospolyhelper.features.ui.utilities.addresses

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.databinding.PageAddressesBinding
import com.mospolytech.mospolyhelper.domain.addresses.model.AddressMap
import com.mospolytech.mospolyhelper.features.utils.RecyclerViewInViewPagerHelper

class AddressesPageAdapter(
    private val addressMap: AddressMap
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.page_addresses, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind()
    }

    override fun getItemCount() = addressMap.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val viewBinding by viewBinding(PageAddressesBinding::bind)

        init {
            viewBinding.recyclerAddresses.layoutManager = LinearLayoutManager(view.context)
            val scale = view.context.resources.displayMetrics.density
            viewBinding.recyclerAddresses.addItemDecoration(AddressesAdapter.ItemDecoration((8 * scale + 0.5f).toInt()))
            viewBinding.recyclerAddresses.addOnItemTouchListener(RecyclerViewInViewPagerHelper)
        }

        fun bind() {
            val pair = addressMap.entries.toList()[bindingAdapterPosition]

            viewBinding.recyclerAddresses.adapter = AddressesAdapter(pair.value, pair.key)
            viewBinding.recyclerAddresses.adapter?.notifyDataSetChanged()
        }
    }
}