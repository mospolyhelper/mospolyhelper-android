package com.mospolytech.mospolyhelper.features.ui.account.students.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.databinding.ItemLoadingBinding
import com.mospolytech.mospolyhelper.utils.inflate
import com.mospolytech.mospolyhelper.utils.show
import kotlinx.android.synthetic.main.item_loading.view.*

class PagingLoadingAdapter(private val retry: () -> Unit): LoadStateAdapter<PagingLoadingAdapter.LoadingStateViewHolder>() {

    override fun onBindViewHolder(holder: LoadingStateViewHolder, loadState: LoadState) {
        holder.bindState(loadState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): LoadingStateViewHolder {
        return LoadingStateViewHolder(parent.inflate(R.layout.item_loading), retry)
    }

    class LoadingStateViewHolder(itemView: View, retry: () -> Unit) :
        RecyclerView.ViewHolder(itemView) {

        private val viewBinding by viewBinding(ItemLoadingBinding::bind)

        private val tvErrorMessage: TextView = viewBinding.tvError
        private val progressBar: ProgressBar = viewBinding.progressBar
        private val btnRetry: Button = viewBinding.buttonRetry

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

}