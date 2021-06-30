package com.mospolytech.mospolyhelper.features.utils.diff_utils

import androidx.recyclerview.widget.DiffUtil
import com.mospolytech.mospolyhelper.domain.utilities.news.model.NewsPreview

object NewsDiffUtils : DiffUtil.ItemCallback<NewsPreview>() {
    override fun areItemsTheSame(oldItem: NewsPreview, newItem: NewsPreview): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: NewsPreview, newItem: NewsPreview): Boolean {
        return oldItem == newItem
    }
}