package com.mospolytech.mospolyhelper.features.ui.account.messaging.adapter

import android.content.Intent
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.databinding.ItemFileBinding
import com.mospolytech.mospolyhelper.domain.account.messaging.model.Attachment
import com.mospolytech.mospolyhelper.utils.inflate

class FilesAdapter(private val items: List<Attachment>):RecyclerView.Adapter<FilesAdapter.FilesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilesViewHolder {
        return FilesViewHolder(parent.inflate(R.layout.item_file))
    }

    override fun onBindViewHolder(holder: FilesViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    class FilesViewHolder(val view: View): RecyclerView.ViewHolder(view) {

        private val viewBinding by viewBinding(ItemFileBinding::bind)

        fun bind(item: Attachment) {
            with(viewBinding) {
                fileName.text = item.name
                val url = "https://e.mospolytech.ru/down.php?f=${item.url}"
                itemView.setOnClickListener {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    ContextCompat.startActivity(itemView.context, browserIntent, null)
                }
            }
        }

    }
}