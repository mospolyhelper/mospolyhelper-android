package com.mospolytech.mospolyhelper.features.ui.account.students.adapter

import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.utils.inflate

class PagingLoadingAdapter(private val retry: () -> Unit): LoadStateAdapter<ViewHolderLoadingState>() {

    override fun onBindViewHolder(holder: ViewHolderLoadingState, loadState: LoadState) {
        holder.bindState(loadState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): ViewHolderLoadingState {
        return ViewHolderLoadingState(parent.inflate(R.layout.item_loading), retry)
    }

}