package com.mospolytech.mospolyhelper.features.ui.account.students.adapter

import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.mospolytech.mospolyhelper.utils.show
import kotlinx.android.synthetic.main.item_loading.view.*

class ViewHolderLoadingState(itemView: View, retry: () -> Unit) :
    RecyclerView.ViewHolder(itemView) {

    private val tvErrorMessage: TextView = itemView.tv_error
    private val progressBar: ProgressBar = itemView.progressBar
    private val btnRetry: Button = itemView.buttonRetry

    init {
        btnRetry.setOnClickListener {
            retry.invoke()
        }
    }

    fun bindState(loadState: LoadState) {
        if (loadState is LoadState.Error) {
            tvErrorMessage.show()
        }
        progressBar.isVisible = loadState is LoadState.Loading
        tvErrorMessage.isVisible = loadState !is LoadState.Loading
        btnRetry.isVisible = loadState !is LoadState.Loading

    }

}
