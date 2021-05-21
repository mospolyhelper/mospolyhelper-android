package com.mospolytech.mospolyhelper.features.ui.utilities.addresses

import android.graphics.Rect
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.databinding.ItemAddressBinding
import com.mospolytech.mospolyhelper.domain.addresses.model.Address

class AddressesAdapter(
    var addresses: List<Address>,
    val type: String
) : RecyclerView.Adapter<AddressesAdapter.ViewHolder>() {
    override fun getItemCount() = addresses.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_address, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(addresses[position])
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val viewBinding by viewBinding(ItemAddressBinding::bind)

        fun bind(address: Address) {
            val spannedDescription = HtmlCompat.fromHtml(address.description, HtmlCompat.FROM_HTML_MODE_LEGACY)
            val spannedTitle = HtmlCompat.fromHtml(address.title, HtmlCompat.FROM_HTML_MODE_LEGACY)
            val builder = SpannableStringBuilder()
            builder.append(spannedTitle)
                .append('\n')
                .append(spannedDescription)
            viewBinding.text.text = builder
        }
    }

    class ItemDecoration(private val offset: Int) : RecyclerView.ItemDecoration() {
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